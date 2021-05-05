package me.drizzy.practice.match;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.hcf.HCFManager;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.match.events.MatchEndEvent;
import me.drizzy.practice.match.events.MatchPlayerSetupEvent;
import me.drizzy.practice.match.task.BridgePlayerTask;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.location.BlockUtil;
import me.drizzy.practice.util.location.LocationUtils;
import me.drizzy.practice.util.other.Cooldown;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.other.TaskUtil;
import me.drizzy.practice.util.other.TimeUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MatchListener implements Listener {
    
    public Array plugin = Array.getInstance();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (!profile.getMatch().isHCFMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    if (match.getKit().getGameRules().isSpleef()) {
                        event.setCancelled(true);
                        return;
                    }
                    final Arena arena = match.getArena();
                    final int y = (int) event.getBlockPlaced().getLocation().getY();
                    if (y > arena.getMaxBuildHeight()) {
                        event.getPlayer().sendMessage(Locale.MATCH_MAX_BUILD.toString());
                        event.setCancelled(true);
                        return;
                    }
                    if (arena instanceof TheBridgeArena) {
                        TheBridgeArena standaloneArena = (TheBridgeArena) arena;
                        if (standaloneArena.getBlueCuboid() != null && standaloneArena.getBlueCuboid().contains(event.getBlockPlaced())) {
                            event.getPlayer().sendMessage(Locale.MATCH_BRIDGE_BLOCK.toString());
                            event.setCancelled(true);
                            return;
                        }
                        if (standaloneArena.getRedCuboid() != null && standaloneArena.getRedCuboid().contains(event.getBlockPlaced())) {
                            event.getPlayer().sendMessage(Locale.MATCH_BRIDGE_BLOCK.toString());
                            event.setCancelled(true);
                            return;
                        }
                    }
                    match.getPlacedBlocks().add(event.getBlock().getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInFight()) {
                if (profile.getMatch().getKit().getGameRules().isBridge()) {
                    if (player.getLocation().getBlock().getType() == Material.ENDER_PORTAL ||
                        player.getLocation().getBlock().getType() == Material.ENDER_PORTAL_FRAME) {
                        TheBridgeMatch match = (TheBridgeMatch) profile.getMatch();

                        if (match.getState() == MatchState.ENDING) return;

                        if (LocationUtils.isTeamPortal(player)) {
                            new BridgePlayerTask(match, player).run();
                            player.sendMessage(Locale.MATCH_BRIDGE_WRONG_PORTAL.toString());
                            return;
                        }

                        if (match.getCaughtPlayers().contains(player)) return;

                        for ( TeamPlayer teamPlayer : match.getTeamPlayers() ) {
                            Player other = teamPlayer.getPlayer();

                            other.sendMessage(Locale.MATCH_BRIDGE_SCORED.toString()
                                    .replace("<relation_color_scored>", match.getRelationColor(other, player).toString())
                                    .replace("<scored_name>", player.getName()));

                            teamPlayer.getPlayer().teleport(teamPlayer.getPlayerSpawn());
                        }
                        match.handleDeath(match.getOpponentPlayer(player), null, false);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockFromEvent(BlockFromToEvent event) {
        Match.getMatches().forEach(match -> {
            Arena arena = match.getArena();
            int y = (int) event.getToBlock().getLocation().getY();

            if (y > arena.getMaxBuildHeight()) {
                event.setCancelled(true);
                return;
            }

            int id = event.getBlock().getTypeId();
            if (id >= 8 && id <= 11) {
                Block b = event.getToBlock();
                int toid = b.getTypeId();
                if (toid == 0 && BlockUtil.generatesCobble(id, b)) {
                    event.setCancelled(true);
                }
            }

            if (arena instanceof TheBridgeArena) {
                TheBridgeArena standaloneArena = (TheBridgeArena) arena;
                if (standaloneArena.getBlueCuboid() != null && standaloneArena.getBlueCuboid().contains(event.getToBlock())) {
                    event.setCancelled(true);
                    return;
                }
                if (standaloneArena.getRedCuboid() != null && standaloneArena.getRedCuboid().contains(event.getToBlock())) {
                    event.setCancelled(true);
                }
            }

            Location l = event.getToBlock().getLocation();
            List<UUID> playersinarena = new ArrayList<>();
            for (Entity entity : BlockUtil.getNearbyEntities(l, 50)) {
                if (entity instanceof Player) {
                    playersinarena.add(((Player) entity).getPlayer().getUniqueId());
                }
            }
            if (playersinarena.size() > 0) {
                final Profile profile = Profile.getByUuid(playersinarena.get(0));
                if (profile.isInFight()) {
                    final Match match2 = profile.getMatch();
                    match2.getPlacedBlocks().add(event.getToBlock().getLocation());
                }
            }

        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInFight()) {
            Match match=profile.getMatch();
            if (!profile.getMatch().isHCFMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    if (match.getKit().getGameRules().isSpleef()) {
                        if (event.getBlock().getType() == Material.SNOW_BLOCK ||
                            event.getBlock().getType() == Material.SNOW) {
                            match.getChangedBlocks().add(event.getBlock().getState());
                            event.getBlock().setType(Material.AIR);
                            event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
                            event.getPlayer().updateInventory();
                        } else {
                            event.setCancelled(true);
                        }
                    } else if (match.getKit().getGameRules().isBoxUHC()) {
                        if (event.getBlock().getType() == Material.WOOD) {
                            match.getChangedBlocks().add(event.getBlock().getState());
                            event.getBlock().setType(Material.AIR);
                            event.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD, 1));
                            event.getPlayer().updateInventory();
                        } else {
                            event.setCancelled(true);
                        }
                    } else if (match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
                        event.getPlayer().getInventory().addItem(new ItemBuilder(event.getBlock().getType()).durability(event.getBlock().getData()).build());
                        event.getPlayer().updateInventory();
                        event.getBlock().setType(Material.AIR);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (!profile.getMatch().isHCFMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    Arena arena = match.getArena();
                    Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                    int y = (int) block.getLocation().getY();
                    if (y > arena.getMaxBuildHeight()) {
                        event.getPlayer().sendMessage(Locale.MATCH_MAX_BUILD.toString());
                        event.setCancelled(true);
                        return;
                    }
                    match.getPlacedBlocks().add(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItemEvent(final PlayerPickupItemEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
            return;
        }
        if (profile.isInFight()) {
            if (!profile.getMatch().getTeamPlayer(event.getPlayer()).isAlive()) {
                event.setCancelled(true);
                return;
            }
            if (event.getItem().getItemStack().getType().name().contains("BOOK")) {
                event.setCancelled(true);
            }
            for ( ItemStack itemStack :  HCFManager.getHCFKitItems() ) {
                if (event.getItem().getItemStack() == itemStack) {
                    event.getItem().remove();
                    event.setCancelled(true);
                }
            }
            if (profile.getMatch().isSoloMatch() && (!profile.getMatch().getOpponentTeamPlayer(event.getPlayer()).isAlive() || profile.getMatch().getOpponentTeamPlayer(event.getPlayer()).isDisconnected()))  {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
        if (event.getItemDrop().getItemStack().getType() == Material.BOOK || event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().getType() == Material.INK_SACK) {
            event.getItemDrop().remove();
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.DIAMOND_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.BARD_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.ARCHER_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.ROGUE_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (profile.isInSomeSortOfFight()) {
            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                event.getItemDrop().setTicksLived(5940);
                return;
            }
            if (profile.getMatch() != null) {
                profile.getMatch().getEntities().add(event.getItemDrop());
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(final ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.DIAMOND_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.BARD_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.ARCHER_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarType.ROGUE_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        //event.getEntity().spigot().respawn();
        Player player = event.getEntity().getPlayer();
        Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
        Match match = profile.getMatch();

        event.setDeathMessage(null);

        if (profile.isInFight()) {

            //If match is TheBridge then handle it seperately
            if (profile.getMatch().isTheBridgeMatch()) {
                event.getDrops().clear();
                PlayerUtil.reset(player);
                TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;
                for ( Player player2 : bridgeMatch.getPlayersAndSpectators() ) {
                    if (player.getKiller() == null) {
                        player2.sendMessage(Locale.MATCH_DIED.toString()
                                .replace("<relation_color>", bridgeMatch.getRelationColor(player2, player).toString())
                                .replace("<participant_name>", player.getName()));
                    } else {
                        player2.sendMessage(Locale.MATCH_KILLED.toString()
                                .replace("<relation_color_dead>", bridgeMatch.getRelationColor(player2, player).toString())
                                .replace("<dead_name>", player.getName())
                                .replace("<relation_color_killer>", bridgeMatch.getRelationColor(player2, player.getKiller()).toString())
                                .replace("<killer_name>", player.getKiller().getName()));
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, new BridgePlayerTask(bridgeMatch, player), 2L);
                return;
            }

            //Reset the Player's damage ticks and knockback profile
            event.getEntity().getPlayer().setMaximumNoDamageTicks(20);
            event.getEntity().getPlayer().setNoDamageTicks(20);
            event.getDrops().clear();
            plugin.getNMSManager().getKnockbackType().applyDefaultKnockback(player);

            if (PlayerUtil.getLastDamager(event.getEntity()) instanceof CraftPlayer) {
                Player killer = (Player) PlayerUtil.getLastDamager(event.getEntity());
                match.handleDeath(player, killer, false);
            } else if (player.getKiller() != null) {
                match.handleDeath(player, player.getKiller(), false);
            } else {
                match.handleDeath(player, null, false);
            }

            //PotPvP aka Lunar Death Animation
            player.teleport(player.getLocation().add(0.0, 0.5, 0.0));
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        event.setRespawnLocation(event.getPlayer().getLocation());
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInFight()) {
            profile.getMatch().handleRespawn(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunchEvent(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof ThrownPotion && event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getEntity().getShooter();
            final Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isInFight() && shooterData.getMatch().isFighting()) {
                shooterData.getMatch().getTeamPlayer(shooter).incrementPotionsThrown();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHitEvent(final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getEntity().getShooter();
            final Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isInFight()) {
                shooterData.getMatch().getEntities().add(event.getEntity());
                shooterData.getMatch().getTeamPlayer(shooter).handleHit();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(final PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getPotion().getShooter();
            final Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isSpectating()) {
                event.setCancelled(true);
            }
            if (shooterData.isInFight() && event.getIntensity(shooter) <= 0.5) {
                shooterData.getMatch().getTeamPlayer(shooter).incrementPotionsMissed();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            final Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
            if (profile.isInFight() && !profile.getMatch().isHCFMatch()  && !profile.getMatch().getKit().getGameRules().isHealthRegeneration()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInFight()) {
                final Match match = profile.getMatch();

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (profile.getMatch().getKit().getGameRules().isVoidSpawn()
                       || profile.getMatch().isTheBridgeMatch()) {

                        event.setDamage(0.0);
                        player.setFallDistance(0);
                        player.setHealth(20.0);

                        if (profile.getMatch().isTheBridgeMatch()) {
                            TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;

                                PlayerUtil.reset(player);
                                for ( Player player2 : bridgeMatch.getPlayersAndSpectators() ) {
                                    player2.sendMessage(Locale.MATCH_DIED.toString()
                                            .replace("<relation_color>", bridgeMatch.getRelationColor(player2, player).toString())
                                            .replace("<participant_name>", player.getName()));
                                }

                            Bukkit.getScheduler().runTaskLater(plugin, new BridgePlayerTask(bridgeMatch, player), 2L);
                            return;
                        }

                        player.teleport(match.getTeamPlayer(player).getPlayerSpawn());
                        return;
                    }

                    profile.getMatch().handleDeath(player, null, false);
                    return;
                }

                if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if (profile.getMatch() != null) {
                        if (profile.getMatch().isTheBridgeMatch() || profile.getMatch().getKit() != null && profile.getMatch().getKit().getGameRules().isDisableFallDamage()) {
                            event.setCancelled(true);
                        }
                    }
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.LAVA && !profile.getMatch().isHCFMatch()  && profile.getMatch().getKit().getGameRules().isLavaKill()) {
                    profile.getMatch().handleDeath(player, null, false);
                    return;
                }

                if (!profile.getMatch().isHCFMatch()  && profile.getMatch().getKit().getGameRules().isParkour()) {
                    event.setCancelled(true);
                    return;
                }

                if (!profile.getMatch().isFighting()) {
                    event.setCancelled(true);
                    return;
                }

                if ((profile.getMatch().isTeamMatch() || profile.getMatch().isHCFMatch()) && !profile.getMatch().getTeamPlayer(player).isAlive()) {
                    event.setCancelled(true);
                    return;
                }

                if (!profile.getMatch().isHCFMatch()  && profile.getMatch().getKit().getGameRules().isSumo()) {
                    event.setDamage(0.0);
                    player.setHealth(20.0);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLow(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        Match match = profile.getMatch();
        if (profile.isInFight()) {
            if (player.getLocation().getBlockY() <= plugin.getEssentials().getMeta().getVoidSpawnLevel()) {
            if (match.getCatcher().contains(player)) return;
                match.getCatcher().add(player);
                if (profile.getMatch().isTheBridgeMatch()) {
                    TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;
                    PlayerUtil.reset(player);
                    for ( Player player2 : bridgeMatch.getPlayersAndSpectators() ) {
                        player2.sendMessage(Locale.MATCH_DIED.toString()
                                .replace("<relation_color>", bridgeMatch.getRelationColor(player2, player).toString())
                                .replace("<participant_name>", player.getName()));
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, new BridgePlayerTask(bridgeMatch, player), 2L);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        Player attacker;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else {
            if (!(event.getDamager() instanceof Projectile)) {
                event.setCancelled(true);
                return;
            }
            if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                event.setCancelled(true);
                return;
            }
            attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (attacker != null && event.getEntity() instanceof Player) {
            final Player damaged = (Player) event.getEntity();
            final Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
            final Profile attackerProfile = Profile.getByUuid(attacker.getUniqueId());
            if (attackerProfile.isSpectating() || damagedProfile.isSpectating()) {
                event.setCancelled(true);
                return;
            }
            if (damagedProfile.isInFight() && attackerProfile.isInFight()) {
                final Match match = attackerProfile.getMatch();
                if (!match.isHCFMatch()  && match.getKit().getGameRules().isSpleef() && !(event.getDamager() instanceof Projectile)) {
                    event.setCancelled(true);
                }
                if (!damagedProfile.getMatch().isHCFMatch() && damagedProfile.getMatch().getKit().getGameRules().isSpleef() && !(event.getDamager() instanceof Projectile)) {
                    event.setCancelled(true);
                    return;
                }
                if (!damagedProfile.getMatch().getMatchId().equals(attackerProfile.getMatch().getMatchId())) {
                    event.setCancelled(true);
                    return;
                }
                if (!match.getTeamPlayer(damaged).isAlive() || (!match.getTeamPlayer(attacker).isAlive() && !match.isFreeForAllMatch())) {
                    event.setCancelled(true);
                    return;
                }
                if (match.isSoloMatch() || match.isFreeForAllMatch() || match.isTheBridgeMatch()) {
                    attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
                    damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();
                    if (event.getDamager() instanceof Arrow) {
                        double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;
                        if (match.getKit().getGameRules().isBowHP()) {
                            if (!attacker.getName().equalsIgnoreCase(damaged.getName())) {
                                attacker.sendMessage(Locale.MATCH_BOW_HIT.toString()
                                        .replace("<damaged_name>", damaged.getName())
                                        .replace("<damaged_health>", String.valueOf(health)));
                            }
                        }
                    }
                } else if (match.isTeamMatch() || match.isHCFMatch() ) {
                    final Team attackerTeam = match.getTeam(attacker);
                    final Team damagedTeam = match.getTeam(damaged);
                    if (attackerTeam == null || damagedTeam == null) {
                        event.setCancelled(true);
                    } else if (attackerTeam.equals(damagedTeam)) {
                        if (!damaged.getUniqueId().equals(attacker.getUniqueId())) {
                            event.setCancelled(true);
                        }
                    } else {
                        attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
                        damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();
                        if (event.getDamager() instanceof Arrow) {
                            final double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;
                            if (match.getKit() == null || match.getKit().getGameRules().isBowHP()) {
                                if (!attacker.getName().equalsIgnoreCase(damaged.getName())) {
                                    attacker.sendMessage(Locale.MATCH_BOW_HIT.toString()
                                            .replace("<damaged_name>", damaged.getName())
                                            .replace("<damaged_health>", String.valueOf(health)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType().equals(Material.POTION) && plugin.getEssentials().getMeta().isRemoveBottles()) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> 
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR)), 1L);
        }
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInSomeSortOfFight()) {
                if (profile.getMatch() != null && profile.getMatch().getKit() != null && !profile.getMatch().isHCFMatch()) {
                    if (profile.getMatch().getKit().getGameRules().isAntiFoodLoss()) {
                        if (event.getFoodLevel() >= 20) {
                            event.setFoodLevel(20);
                            player.setSaturation(20.0f);
                        }
                    } else {
                        event.setCancelled(false);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
        if (profile.isInFight() && profile.getMatch().getState() == MatchState.FIGHTING) {
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        } else if (profile.isInMatch() && profile.getMatch().getState() == MatchState.FIGHTING) {
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        } else if (profile.isInMatch() || profile.isInFight() && profile.getMatch().getState() == MatchState.STARTING) {
            profile.getMatch().getTask().cancel();
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(final InventoryInteractEvent event) {
        final Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    /**
     * The main code for CPS Counter
     * Fully coded by Veltus @ PurgeCommunity
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(final PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("LEFT")) {
            final Profile profile = Profile.getByPlayer(event.getPlayer());

            if (profile.isInFight() || profile.isInEvent() || profile.isInTournament()) {
                try {
                    TeamPlayer player=profile.getMatch().getTeamPlayer(event.getPlayer());

                    List<Long> list;

                    if (player.getCpsMap().containsKey(player.getUuid())) {
                        list = player.getCpsMap().get(player.getUuid());
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(System.currentTimeMillis());

                    player.getCpsMap().put(event.getPlayer().getUniqueId(), list);
                } catch (Exception ignored) {}
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {

            EnderPearl enderPearl = (EnderPearl) projectile;
            ProjectileSource source = enderPearl.getShooter();

            if (source instanceof Player) {

                Player shooter = (Player) source;
                Profile profile = Profile.getByUuid(shooter.getUniqueId());

                if (profile.isInFight()) {

                    if (!profile.getEnderpearlCooldown().hasExpired()) {
                        String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                        String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");

                        shooter.sendMessage(Locale.MATCH_PEARL_COOLDOWN.toString().replace("<cooldown>", time + " " + context));
                        shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));

                        event.setCancelled(true);
                    } else {
                        profile.setEnderpearlCooldown(new Cooldown(16000L));
                        profile.getMatch().onPearl(shooter, enderPearl);
                    }
                }
            }
        } else if (projectile instanceof Arrow) {
            Arrow enderPearl = (Arrow) projectile;
            ProjectileSource source = enderPearl.getShooter();

            if (source instanceof Player) {
                Player shooter = (Player) source;
                Profile profile = Profile.getByPlayer(shooter);

                if (profile.isInFight() && profile.getMatch().isTheBridgeMatch()) {
                    if (!profile.getBowCooldown().hasExpired()) {
                        final String time = TimeUtil.millisToSeconds(profile.getBowCooldown().getRemaining());
                        final String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");

                        shooter.sendMessage(Locale.MATCH_BOW_COOLDOWN.toString().replace("<cooldown>", time + " " + context));

                        event.setCancelled(true);
                    } else {
                        profile.setBowCooldown(new Cooldown(10000L));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportPearl(final PlayerTeleportEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && profile.isInFight()) {
            profile.getMatch().removePearl(event.getPlayer());
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
            if (event.getItem() != null && event.getAction().name().contains("RIGHT") && profile.isInFight()) {
                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarType.DEFAULT_KIT))) {
                        final KitInventory kitInventory=profile.getMatch().getKit().getKitInventory();
                        event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                        event.getPlayer().getInventory().setContents(kitInventory.getContents());
                        event.getPlayer().getActivePotionEffects().clear();
                        if (profile.getMatch().getKit().getKitInventory().getEffects() != null) {
                            event.getPlayer().addPotionEffects(profile.getMatch().getKit().getKitInventory().getEffects());
                        }

                        event.getPlayer().updateInventory();
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarType.DIAMOND_KIT))) {
                        final KitInventory kitInventory=Objects.requireNonNull(Kit.getByName("HCFDIAMOND")).getKitInventory();
                        event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                        event.getPlayer().getInventory().setContents(kitInventory.getContents());
                        event.getPlayer().getActivePotionEffects().clear();
                        if (kitInventory.getEffects() != null) {
                            event.getPlayer().addPotionEffects(kitInventory.getEffects());
                        }
                        event.getPlayer().updateInventory();
                        plugin.getHCFManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarType.BARD_KIT))) {
                        final KitInventory kitInventory=Objects.requireNonNull(Kit.getByName("HCFBARD")).getKitInventory();
                        event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                        event.getPlayer().getInventory().setContents(kitInventory.getContents());
                        event.getPlayer().getActivePotionEffects().clear();
                        if (kitInventory.getEffects() != null) {
                            event.getPlayer().addPotionEffects(kitInventory.getEffects());
                        }
                        event.getPlayer().updateInventory();
                        plugin.getHCFManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarType.ARCHER_KIT))) {
                        final KitInventory kitInventory=Objects.requireNonNull(Kit.getByName("HCFARCHER")).getKitInventory();
                        event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                        event.getPlayer().getInventory().setContents(kitInventory.getContents());
                        event.getPlayer().getActivePotionEffects().clear();
                        if (kitInventory.getEffects() != null) {
                            event.getPlayer().addPotionEffects(kitInventory.getEffects());
                        }
                        event.getPlayer().updateInventory();
                        plugin.getHCFManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarType.ROGUE_KIT))) {
                        final KitInventory kitInventory=Objects.requireNonNull(Kit.getByName("HCFROGUE")).getKitInventory();
                        event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                        event.getPlayer().getInventory().setContents(kitInventory.getContents());
                        event.getPlayer().getActivePotionEffects().clear();
                        if (kitInventory.getEffects() != null) {
                            event.getPlayer().addPotionEffects(kitInventory.getEffects());
                        }
                        event.getPlayer().updateInventory();
                        plugin.getHCFManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                }
                if (!profile.getMatch().isHCFMatch() && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().hasLore()) {
                    final String displayName = CC.translate(event.getItem().getItemMeta().getDisplayName());
                    if (displayName.endsWith(" (Right-Click)")) {
                        final String kitName=displayName.replace(" (Right-Click)", "");
                        for ( final KitInventory kitInventory2 : profile.getStatisticsData().get(profile.getMatch().getKit()).getLoadouts() ) {
                            if (kitInventory2 != null && ChatColor.stripColor(kitInventory2.getCustomName()).equals(ChatColor.stripColor(kitName))) {
                                event.getPlayer().getInventory().setArmorContents(kitInventory2.getArmor());
                                event.getPlayer().getInventory().setContents(kitInventory2.getContents());
                                event.getPlayer().getActivePotionEffects().clear();
                                event.getPlayer().addPotionEffects(profile.getMatch().getKit().getKitInventory().getEffects());
                                event.getPlayer().updateInventory();
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }

                Player player = event.getPlayer();
                if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && player.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
                    final int health=(int) player.getHealth();
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
                if ((event.getItem().getType() == Material.ENDER_PEARL || (event.getItem().getType() == Material.POTION && event.getItem().getDurability() >= 16000)) && profile.isInFight() && profile.getMatch().isStarting()) {
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }
                if (event.getItem().getType() == Material.ENDER_PEARL && event.getClickedBlock() == null) {
                    if (!profile.isInFight() || (profile.isInFight() && !profile.getMatch().isFighting())) {
                        event.setCancelled(true);
                        return;
                    }
                    if (profile.getMatch().isStarting()) {
                        event.setCancelled(true);
                    }
                }
            }
    }

    @EventHandler
    public void onPressurePlate(final PlayerInteractEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        if (profile.isInFight() && match.isSoloMatch() && match.getKit().getGameRules().isParkour()) {
            if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType() == Material.GOLD_PLATE && profile.getPlates() != null) {
                if (profile.getPlates().contains(event.getClickedBlock().getLocation())) return;
                profile.getPlates().add(event.getClickedBlock().getLocation());
                if (match.getOpponentPlayer(event.getPlayer()) != null) {
                    match.handleDeath(event.getPlayer(), null, false);
                } else {
                    match.end();
                }
            }
            if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType() == Material.IRON_PLATE && profile.getPlates() != null) {
                if (profile.getPlates().contains(event.getClickedBlock().getLocation())) return;
                match.getTeamPlayer(event.getPlayer()).setParkourCheckpoint(event.getPlayer().getLocation());
                event.getPlayer().sendMessage(Locale.MATCH_CHECKPOINT.toString());
                profile.getPlates().add(event.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled() || e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        final Location target = e.getTo();
        target.setX(target.getBlockX() + 0.5);
        target.setZ(target.getBlockZ() + 0.5);
        e.setTo(target);
    }

    @EventHandler
    public void onPotionSplash(final PotionSplashEvent event) {
      for( LivingEntity entity : event.getAffectedEntities()) {
            if (entity instanceof Player) {
                final Player player = (Player) entity;
                final Profile profile = Profile.getByUuid(player.getUniqueId());
                if (!profile.isSpectating()) {
                    continue;
                }
                event.setIntensity(player, 0.0);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        final Profile profile = Profile.getByUuid(e.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            profile.getMatch().removeSpectator(e.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamageEntity(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player damaged = (Player) e.getEntity();
            Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
            if (damagedProfile.getMatch() != null) {
                Match match = damagedProfile.getMatch();
                if (match.isEnding()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPearlThrow(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            EnderPearl enderPearl = (EnderPearl) projectile;
            ProjectileSource source = enderPearl.getShooter();

            if (source instanceof Player) {
                Player shooter = (Player) source;
                Profile profile = Profile.getByPlayer(shooter);

                if (profile.getMatch() != null && profile.getMatch().getArena() != null) {
                    if (profile.getMatch().getArena().isDisablePearls()) {
                        shooter.sendMessage(Locale.ERROR_PEARLSDISABLED.toString());
                        shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
