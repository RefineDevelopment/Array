package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.events.match.MatchPlayerSetupEvent;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.kit.BattleRushMatch;
import xyz.refinedev.practice.match.types.kit.MLGRushMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.hotbar.HotbarItem;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MatchListener implements Listener {

    public final Array plugin;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        Arena arena = match.getArena();

        if (!profile.isInFight()) return;
        if (!match.getKit().getGameRules().isBuild() || !profile.getMatch().isFighting()) {
            event.setCancelled(true);
            return;
        }

        if (match.getKit().getGameRules().isSpleef()) {
            event.setCancelled(true);
            return;
        }

        int x = (int) block.getLocation().getX();
        int y = (int) block.getLocation().getY();
        int z = (int) block.getLocation().getZ();

        if (y > arena.getMaxBuildHeight()) {
            player.sendMessage(Locale.MATCH_MAX_BUILD.toString());
            event.setCancelled(true);
            return;
        }

        //Checks if you are placing blocks near the portal
        boolean occupied = false;

        if (match instanceof SoloBridgeMatch) {
            SoloBridgeMatch soloBridgeMatch = (SoloBridgeMatch) match;
            occupied = soloBridgeMatch.isVolatileLocation(block.getLocation());
        } else if (match instanceof TeamBridgeMatch) {
            TeamBridgeMatch teamBridgeMatch = (TeamBridgeMatch) match;
            occupied = teamBridgeMatch.isVolatileLocation(block.getLocation());
        } else if (match instanceof BattleRushMatch) {
            BattleRushMatch battleRushMatch = (BattleRushMatch) match;
            occupied = battleRushMatch.isVolatileLocation(block.getLocation());
        }

        if (occupied) {
            player.sendMessage(Locale.MATCH_PLACE_BLOCK.toString());
            event.setCancelled(true);
            return;
        }

        if (arena.getMax() == null || arena.getMin() == null) return;

        Cuboid cuboid = new Cuboid(arena.getMax(), arena.getMin());
        if (x >= cuboid.getX1() && x <= cuboid.getX2() && y >= cuboid.getY1() && y <= cuboid.getY2() && z >= cuboid.getZ1() && z <= cuboid.getZ2()) {
            match.getPlacedBlocks().add(block.getLocation());
            Location down = block.getLocation().subtract(0, 1, 0);
            if (down.getBlock().getType() == Material.GRASS) {
                match.getChangedBlocks().add(down.getBlock().getState());
            }
        } else {
            player.sendMessage(Locale.MATCH_BUILD_OUTSIDE.toString());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPortal(EntityPortalEnterEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (!profile.isInFight()) return;
        if (player.getLocation().getBlock().getType() != Material.ENDER_PORTAL && player.getLocation().getBlock().getType() != Material.ENDER_PORTAL_FRAME) return;

        if (match.isBattleRushMatch()) {
            BattleRushMatch battleRushMatch = (BattleRushMatch) match;
            battleRushMatch.handlePortal(plugin, player);
            return;
        }

        if (match instanceof SoloBridgeMatch) {
            SoloBridgeMatch soloBridgeMatch = (SoloBridgeMatch) match;
            soloBridgeMatch.handlePortal(plugin, player);
        } else if (match instanceof TeamBridgeMatch) {
            TeamBridgeMatch teamBridgeMatch = (TeamBridgeMatch) match;
            teamBridgeMatch.handlePortal(plugin, player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockFromEvent(BlockFromToEvent event) {
        if (Math.abs(event.getBlock().getX()) > 300 && Math.abs(event.getBlock().getZ()) > 300) {
            Match match = null;

            for ( Match filterMatch : this.plugin.getMatchManager().getMatches() ) {
                if (filterMatch.getPlacedBlocks().contains(event.getBlock().getLocation())) {
                    match = filterMatch;
                }
            }

            if (match == null) {
                event.setCancelled(true);
                return;
            }

            match.getPlacedBlocks().add(event.getToBlock().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        Block block = event.getBlock();

        if (!profile.isInFight()) return;
        if (!match.getKit().getGameRules().isBuild() || !profile.getMatch().isFighting()) {
            event.setCancelled(true);
            return;
        }

        if (match.getKit().getGameRules().isSpleef()) {
            if (block.getType() == Material.SNOW_BLOCK || block.getType() == Material.SNOW) {
                match.getChangedBlocks().add(block.getState());
                block.setType(Material.AIR);
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
                player.updateInventory();
            } else {
                event.setCancelled(true);
            }
        } else if (match.getKit().getGameRules().isBoxuhc()) {
            if (block.getType() == Material.WOOD) {
                match.getChangedBlocks().add(block.getState());
                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemBuilder(block.getType()).build());
                player.updateInventory();
            } else {
                event.setCancelled(true);
            }
        } else if (match.getKit().getGameRules().isBedwars() || match.getKit().getGameRules().isMlgRush()) {
            if (block.getType() == Material.BED || block.getType() == Material.BED_BLOCK) {

                if (match instanceof SoloBedwarsMatch) {
                    SoloBedwarsMatch soloBedwarsMatch = (SoloBedwarsMatch) match;
                    match.getChangedBlocks().add(block.getState());
                    block.setType(Material.AIR);
                    soloBedwarsMatch.handleBed(plugin, player);
                    block.setType(Material.BED);
                } else if (match instanceof TeamBedwarsMatch){
                    TeamBedwarsMatch teamBedwarsMatch = (TeamBedwarsMatch) match;
                    //teamBedwarsMatch.handleBed(player);
                } else if (match instanceof MLGRushMatch) {
                    event.setCancelled(true);

                    MLGRushMatch mlgRushMatch = (MLGRushMatch) match;
                    mlgRushMatch.handleBed(plugin, player);
                }
            }
        } else if (!match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        Arena arena = match.getArena();
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (!profile.isInFight()) return;
        if (!match.getKit().getGameRules().isBuild() || !profile.getMatch().isFighting()) {
            event.setCancelled(true);
            return;
        }

        int x = (int) block.getLocation().getX();
        int z = (int) block.getLocation().getZ();
        int y = (int) block.getLocation().getY();

        if (y > arena.getMaxBuildHeight()) {
            event.getPlayer().sendMessage(Locale.MATCH_MAX_BUILD.toString());
            event.setCancelled(true);
            return;
        }

        Cuboid cuboid = new Cuboid(arena.getMax(), arena.getMin());

        if (x >= cuboid.getX1() && x <= cuboid.getX2() && y >= cuboid.getY1() && y <= cuboid.getY2() && z >= cuboid.getZ1() && z <= cuboid.getZ2()) {
            match.getPlacedBlocks().add(block.getLocation());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (profile.isSpectating()) {
            event.setCancelled(true);
            return;
        }

        if (!profile.isInFight()) return;
        if (!match.getTeamPlayer(event.getPlayer()).isAlive() || match.isEnding()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(event.getPlayer().getUniqueId());

        ItemStack itemStack = event.getItemDrop().getItemStack();
        Material itemType = itemStack.getType();
        String itemTypeName = itemType.name().toLowerCase();
        int heldSlot = player.getInventory().getHeldItemSlot();
        
        if (profile.isSpectating() || event.getItemDrop().getItemStack().getType() == Material.BOOK || event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK) {
            event.setCancelled(true);
        }

        if (profile.isInFight()) {
            profile.getMatch().getEntities().add(event.getItemDrop());
        }

        if (profile.getSettings().isDropProtect()) {
            if (!PlayerUtil.hasOtherInventoryOpen(player) && heldSlot == 0 && (itemTypeName.contains("sword") || itemTypeName.contains("axe") || itemType == Material.BOW)) {
                player.sendMessage(Locale.MATCH_SWORD_DROP.toString());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        Match match = profile.getMatch();

        event.setDeathMessage(null);
        if (!profile.isInFight()) return;

        profile.setDeaths(profile.getDeaths() + 1);

        List<Item> entities = new ArrayList<>();

        event.getDrops().forEach(itemStack -> {
            if (itemStack.getType() != Material.BOOK && itemStack.getType() != Material.ENCHANTED_BOOK && itemStack.getType() != Material.BLAZE_POWDER && itemStack.getType() != Material.GOLD_BARDING && itemStack.getType() != Material.DIAMOND_BARDING) {
                entities.add(event.getEntity().getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack));
            }
        });

        if (match.isTheBridgeMatch() || match.isBattleRushMatch() || match.isBedwarsMatch()) {
            if (PlayerUtil.getLastAttacker(player) instanceof CraftPlayer) {
                Player killer = (Player) PlayerUtil.getLastAttacker(player);
                killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 20F, 15F);
            }
        }

        event.getDrops().clear();
        match.getEntities().addAll(entities);
        this.plugin.getMatchManager().handleDeath(match, player);
    }

    /**
     * Apply the below health display for
     * Build/Show health kit matches
     *
     * @param event {@link MatchPlayerSetupEvent}
     */
    @EventHandler
    public void onStart(MatchPlayerSetupEvent event) {
        if (event.getMatch() == null || event.getMatch().getKit() == null) return;
        if (!event.getMatch().getKit().getGameRules().isBuild() && !event.getMatch().getKit().getGameRules().isShowHealth()) return;
        for ( TeamPlayer otherPlayerTeam : event.getMatch().getTeamPlayers() ) {
            Player otherPlayer = otherPlayerTeam.getPlayer();
            Scoreboard scoreboard = event.getPlayer().getScoreboard();
            Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

            if (objective == null) {
                objective = scoreboard.registerNewObjective("showhealth", "health");
            }

            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.RED + org.apache.commons.lang3.StringEscapeUtils.unescapeJava("\u2764"));
            objective.getScore(otherPlayer.getName()).setScore((int) Math.floor(otherPlayer.getHealth() / 2));
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        event.setRespawnLocation(player.getLocation());

        if (!profile.isInFight()) return;

        profile.getMatch().onRespawn(plugin, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) event.getEntity().getShooter();
        Profile shooterProfile = plugin.getProfileManager().getProfileByUUID(shooter.getUniqueId());

        if (!shooterProfile.isInFight()) return;
        if (!shooterProfile.getMatch().isFighting()) return;

        shooterProfile.getMatch().getTeamPlayer(shooter).incrementPotionsThrown();
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        if (event.getEntity() instanceof Arrow) {
            Player shooter = (Player) event.getEntity().getShooter();
            Profile shooterProfile = plugin.getProfileManager().getProfileByUUID(shooter.getUniqueId());

            if (shooterProfile.isInFight()) {
                shooterProfile.getMatch().getEntities().add(event.getEntity());
                shooterProfile.getMatch().getTeamPlayer(shooter).handleHit();
            }
        } else if (event.getEntity() instanceof Snowball) {
            BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0, 4);
            if (!iterator.hasNext()) return;
            Block hitBlock = iterator.next();

            Player shooter = (Player) event.getEntity().getShooter();
            Profile shooterProfile = plugin.getProfileManager().getProfileByUUID(shooter.getUniqueId());

            if (hitBlock.getType() == Material.SNOW_BLOCK) {
                shooterProfile.getMatch().getChangedBlocks().add(hitBlock.getState());
                hitBlock.setType(Material.AIR);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) event.getPotion().getShooter();
            Profile shooterProfile = plugin.getProfileManager().getProfileByUUID(shooter.getUniqueId());

            if (shooterProfile.isSpectating()) {
                event.setCancelled(true);
            }

            if (shooterProfile.isInFight() && event.getIntensity(shooter) <= 0.5) {
                shooterProfile.getMatch().getTeamPlayer(shooter).incrementPotionsMissed();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(event.getEntity().getUniqueId());
            if (!profile.isInFight()) return;

            if (!profile.getMatch().getKit().getGameRules().isRegen()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

            if (!profile.isInFight()) return;

            Match match = profile.getMatch();
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setDamage(1000.0);
                return;
            }
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                if (match.isTheBridgeMatch() || match.getKit().getGameRules().isDisableFallDamage()) {
                    event.setCancelled(true);
                }
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA && match.getKit().getGameRules().isLavaKill()) {
                this.plugin.getMatchManager().handleDeath(match, player, null, false);
                return;
            }
            if (match.isEnding() || !match.isFighting() || match.getKit().getGameRules().isParkour()) {
                event.setCancelled(true);
                return;
            }
            if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isBoxing() || match.getKit().getGameRules().isBattleRush()) {
                event.setDamage(0.0);
                player.setHealth(20.0);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onVertMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (!profile.isInFight()) return;
        if (match.isEnding()) return;

        if (event.getFrom().getBlockY() - 1 > event.getTo().getBlockY() && match.getArena().getFallDeathHeight() >= event.getTo().getBlockY()) {
            if (match.getKit().getGameRules().isVoidSpawn()) {
                TeamPlayer teamPlayer = match.getTeamPlayer(player);
                if (teamPlayer == null) {
                    player.teleport(match.getMidSpawn());
                } else {
                    player.teleport(teamPlayer.getPlayerSpawn());
                }
                return;
            }

            this.plugin.getMatchManager().handleDeath(match, event.getPlayer());
            if (match.isTheBridgeMatch() || match.isBattleRushMatch()) {
                this.plugin.getMatchManager().handleRespawn(match, event.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Player attacker = null;

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (!(projectile.getShooter() instanceof Player)) {
                event.setCancelled(true);
                return;
            }
            attacker = (Player) projectile.getShooter();
        }

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }

        if (attacker == null) return;
        if (!(event.getEntity() instanceof Player)) return;

        Profile damagedProfile = plugin.getProfileManager().getProfileByUUID(damaged.getUniqueId());
        Profile attackerProfile = plugin.getProfileManager().getProfileByUUID(attacker.getUniqueId());
        if (attackerProfile.isSpectating() || damagedProfile.isSpectating()) {
            event.setCancelled(true);
            return;
        }

        if (!damagedProfile.isInFight()) return;
        if (!attackerProfile.isInFight()) return;

        Match match = attackerProfile.getMatch();
        if (attackerProfile.getMatch().getKit().getGameRules().isSpleef() && !(event.getDamager() instanceof Projectile)) {
            event.setCancelled(true);
        }
        if (damagedProfile.getMatch().getKit().getGameRules().isSpleef() && !(event.getDamager() instanceof Projectile)) {
            event.setCancelled(true);
            return;
        }
        if (!damagedProfile.getMatch().getMatchId().equals(attackerProfile.getMatch().getMatchId())) {
            event.setCancelled(true);
            return;
        }
        if (damagedProfile.getMatch().getState().equals(MatchState.STARTING)) {
            event.setCancelled(true);
            return;
        }
        if ((!match.getTeamPlayer(damaged).isAlive() || !match.getTeamPlayer(attacker).isAlive()) && !match.isFreeForAllMatch()) {
            event.setCancelled(true);
            return;
        }

        if (match.isSoloMatch() || match.isFreeForAllMatch()) {
            Match teamMatch = attackerProfile.getMatch();
            TeamPlayer attackedTeamPlayer = teamMatch.getTeamPlayer(attacker);
            TeamPlayer damagedTeamPlayer = teamMatch.getTeamPlayer(damaged);

            attackedTeamPlayer.handleHit();
            damagedTeamPlayer.resetCombo();

            if (match.isBoxingMatch()) {
                if (attackedTeamPlayer.getHits() >= 100) {
                    this.plugin.getMatchManager().handleDeath(match, damaged);
                    return;
                }
            }

            if (!(event.getDamager() instanceof Arrow)) return;
            double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;

            attacker.sendMessage(Locale.MATCH_BOW_HIT.toString()
                    .replace("<damaged_name>", damaged.getName())
                    .replace("<damaged_health>", String.valueOf(health)));
            return;
        }

        if (!match.isTeamMatch()) return;

        Team attackerTeam = match.getTeam(attacker);
        Team damagedTeam = match.getTeam(damaged);

        if (attackerTeam == null || damagedTeam == null) {
            event.setCancelled(true);
            return;
        }

        if (attackerTeam.equals(damagedTeam)) {
            event.setCancelled(true);
            return;
        }

        attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
        damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();

        if (!match.getKit().getGameRules().isBowHP()) return;
        if (!(event.getDamager() instanceof Arrow)) return;
        double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;

        if (!attacker.getName().equalsIgnoreCase(damaged.getName())) {
            attacker.sendMessage(Locale.MATCH_BOW_HIT.toString()
                    .replace("<damaged_name>", damaged.getName())
                    .replace("<damaged_health>", String.valueOf(health)));
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (!event.getItem().getType().equals(Material.POTION)) return;
        if (!event.getItem().getType().equals(Material.GLASS_BOTTLE)) return;
        if (!plugin.getConfigHandler().isREMOVED_BOTTLES()) return;

        TaskUtil.runLater(() -> event.getPlayer().setItemInHand(new ItemStack(Material.AIR)), 1L);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            Match match = profile.getMatch();

            if (!profile.isInFight()) return;

            if (match.getKit().getGameRules().isAntiFoodLoss()) {
                event.setFoodLevel(20);
            }

            if (match.isFighting()) {
                if (event.getFoodLevel() >= 20) {
                    event.setFoodLevel(20);
                    player.setSaturation(20.0f);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);

        if (!profile.isInFight()) return;
        if (event.getItem() == null || event.getClickedBlock() == null || !event.getAction().name().contains("LEFT")) return;

        TeamPlayer teamPlayer = profile.getMatch().getTeamPlayer(player);
        if (teamPlayer == null) return;

        List<Long> list = teamPlayer.getCpsList();
        try {
            list.add(System.currentTimeMillis());
        } catch (Exception e) {
            list.add(0L);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            EnderPearl enderPearl = (EnderPearl) projectile;
            ProjectileSource source = enderPearl.getShooter();

            if (source instanceof Player) {
                Player shooter = (Player) source;
                Profile profile = plugin.getProfileManager().getProfileByUUID(shooter.getUniqueId());

                if (!profile.isInFight()) return;

                if (!profile.getEnderpearlCooldown().hasExpired()) {
                    String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                    String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");

                    shooter.sendMessage(Locale.MATCH_PEARL_COOLDOWN.toString().replace("<cooldown>", time + " " + context));
                    shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));

                    event.setCancelled(true);
                } else {
                    plugin.getProfileManager().setEnderpearlCooldown(profile, new Cooldown(16000L));
                    profile.getMatch().onPearl(shooter, enderPearl);
                }
            }
        } else if (projectile instanceof Arrow) {
            Arrow enderPearl = (Arrow) projectile;
            ProjectileSource source = enderPearl.getShooter();

            if (source instanceof Player) {
                Player shooter = (Player) source;
                Profile profile = plugin.getProfileManager().getProfileByPlayer(shooter);

                if (!profile.isInFight()) return;
                if (!profile.getMatch().isTheBridgeMatch()) return;

                if (!profile.getBowCooldown().hasExpired()) {
                    String time = TimeUtil.millisToSeconds(profile.getBowCooldown().getRemaining());
                    String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");

                    shooter.sendMessage(Locale.MATCH_BOW_COOLDOWN.toString().replace("<cooldown>", time + " " + context));

                    event.setCancelled(true);
                } else {
                    plugin.getProfileManager().setBowCooldown(profile, new Cooldown(TimeUtil.parseTime(plugin.getConfigHandler().getBOW_COOLDOWN() + "s")));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportPearl(PlayerTeleportEvent event) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(event.getPlayer().getUniqueId());
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && profile.isInFight()) {
            profile.getMatch().removePearl(profile, false);
            Location target = event.getTo();
            target.setX(target.getBlockX() + 0.5);
            target.setZ(target.getBlockZ() + 0.5);
            event.setTo(target);
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        ItemStack item = event.getItem();

        if (profile.isSpectating()) {
            event.setCancelled(true);
            return;
        }

        if (!profile.isInFight()) return;
        if (item == null || !item.hasItemMeta() || !event.getAction().name().contains("RIGHT") || !item.getItemMeta().hasDisplayName()) return;

        HotbarItem hotbarItem = plugin.getHotbarManager().getHotbarItem(HotbarType.DEFAULT_KIT);
        if (event.getItem().isSimilar(hotbarItem.getItem())) {
            match.getKit().applyToPlayer(player);
            event.setCancelled(true);
            return;
        }

        String displayName = CC.translate(event.getItem().getItemMeta().getDisplayName());
        String kitName = displayName.replace(" (Right-Click)", "");
        for ( KitInventory kitInventory : profile.getStatisticsData().get(profile.getMatch().getKit()).getKitInventories() ) {
            if (kitInventory == null) continue;

            if (ChatColor.stripColor(kitInventory.getCustomName()).equals(ChatColor.stripColor(kitName))) {
                kitInventory.applyToPlayer(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (player.getItemInHand().getType().equals(Material.MUSHROOM_SOUP)) {
                int health = (int) player.getHealth();
                if (health == 20) {
                    player.getItemInHand().setType(Material.MUSHROOM_SOUP);
                } else if (health >= 13) {
                    player.setHealth(20.0);
                    player.getItemInHand().setType(Material.BOWL);
                } else {
                    player.setHealth(health + 7);
                    player.getItemInHand().setType(Material.BOWL);
                }
            }
        }

        if (event.getItem().getType() == Material.ENDER_PEARL || (event.getItem().getType() == Material.POTION && event.getItem().getDurability() >= 16000)) {
            if (profile.getMatch().isStarting()) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }

        if (event.getItem().getType() == Material.ENDER_PEARL && event.getClickedBlock() == null) {
            if (!profile.getMatch().isFighting()) {
                event.setCancelled(true);
                return;
            }
            if (profile.getMatch().isStarting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (!profile.isInFight()) return;
        if (!match.getKit().getGameRules().isParkour()) return;
        if (profile.getParkourCheckpoints().contains(event.getClickedBlock().getLocation())) return;

        if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType() == Material.GOLD_PLATE && profile.getParkourCheckpoints() != null) {
            profile.getParkourCheckpoints().add(event.getClickedBlock().getLocation());

            Player opponentPlayer = match.getOpponentPlayer(player);
            if (opponentPlayer == null) {
                this.plugin.getMatchManager().end(match);
                return;
            }

            this.plugin.getMatchManager().handleDeath(match, opponentPlayer);
        }

        if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType() == Material.IRON_PLATE && profile.getParkourCheckpoints() != null) {
            profile.getParkourCheckpoints().add(event.getClickedBlock().getLocation());

            match.getTeamPlayer(player).setParkourCheckpoint(player.getLocation());
            player.sendMessage(Locale.MATCH_CHECKPOINT.toString());
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        for ( LivingEntity entity : event.getAffectedEntities() ) {
            if (!(entity instanceof Player)) return;

            Player player = (Player) entity;
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            if (!profile.isSpectating()) continue;

            event.setIntensity(player, 0.0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (profile.isInFight() || profile.isInMatch()) {
            if (match.isStarting()) match.getTask().cancel();
            this.plugin.getMatchManager().handleDeath(match, player, null, true);
        }

        if (profile.isSpectating()) {
            this.plugin.getMatchManager().removeSpectator(match, player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageEntity(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Profile damagedProfile = plugin.getProfileManager().getProfileByUUID(damaged.getUniqueId());
        Match match = damagedProfile.getMatch();
        if (!damagedProfile.isInFight()) return;

        if (match.isEnding()) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPearlThrow(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof EnderPearl)) return;

        EnderPearl enderPearl = (EnderPearl) projectile;
        ProjectileSource source = enderPearl.getShooter();
        if (!(source instanceof Player)) return;

        Player shooter = (Player) source;
        Profile profile = plugin.getProfileManager().getProfileByPlayer(shooter);

        if (!profile.isInFight()) return;

        if (profile.getMatch().getArena().isDisablePearls()) {
            shooter.sendMessage(Locale.ERROR_PEARLSDISABLED.toString());
            shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
            event.setCancelled(true);
        }
    }
}
