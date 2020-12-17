package me.array.ArrayPractice.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.util.Color;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;

import java.util.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchListener implements Listener {

    @Getter
    private WaterCheckTask waterCheckTask;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInFight()) {
            Match match = profile.getMatch();

            if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    if (match.getKit().getGameRules().isSpleef()) {
                        event.getPlayer().sendMessage(CC.RED + "You can not place blocks in spleef.");
                        event.setCancelled(true);
                        return;
                    }

                    Arena arena = match.getArena();
                    int y = (int) event.getBlockPlaced().getLocation().getY();

                    if (y > arena.getMaxBuildHeight()) {
                        event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
                        event.setCancelled(true);
                        return;
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInFight()) {
            Match match = profile.getMatch();

            if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
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
                    } else if (!match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
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
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInFight()) {
            Match match = profile.getMatch();

            if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                if (match.getKit().getGameRules().isBuild() && profile.getMatch().isFighting()) {
                    Arena arena = match.getArena();
                    Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                    int y = (int) block.getLocation().getY();

                    if (y > arena.getMaxBuildHeight()) {
                        event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
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
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

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
                return;
            }

            Iterator<Entity> entityIterator = profile.getMatch().getEntities().iterator();

            while (entityIterator.hasNext()) {
                Entity entity = entityIterator.next();

                if (entity instanceof Item && entity.equals(event.getItem())) {
                    entityIterator.remove();
                    return;
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }

        if (event.getItemDrop().getItemStack().getType() == Material.BOOK ||
                event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK) {
            event.getItemDrop().remove();
            return;
        }


        if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT))) {
            event.setCancelled(true);
            return;
        } else if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.BARD_KIT))) {
            event.setCancelled(true);
            return;
        }else if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ARCHER_KIT))) {
            event.setCancelled(true);
            return;
        }else if (event.getItemDrop().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ROGUE_KIT))) {
            event.setCancelled(true);
            return;
        }
        if (profile.isInSomeSortOfFight()) {
            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                event.getItemDrop().remove();
                return;
            }
            if (event.getItemDrop().getItemStack().getType() == Material.DIAMOND_SWORD) {
                int swordCount = 0;
                for (ItemStack itemStack : event.getPlayer().getInventory()) {
                    if (itemStack != null && itemStack.getType() == Material.DIAMOND_SWORD) {
                        swordCount++;
                    }
                }
                if (swordCount < 1) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Color.translate("&cYou can not drop the last sword in your inventory."));
                    return;
                }
            }
            if (profile.getMatch() != null) {
                profile.getMatch().getEntities().add(event.getItemDrop());
            }

        }
    }
    @EventHandler
    public void onPlayerDeathEvent(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        } else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.BARD_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        }else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ARCHER_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        }else if (event.getEntity().getItemStack().isSimilar(Hotbar.getItems().get(HotbarItem.ROGUE_KIT))) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

        if (profile.isInFight()) {

            List<Item> entities = new ArrayList<>();

            event.getDrops().forEach(itemStack -> {
                if (!(itemStack.getType() == Material.BOOK || itemStack.getType() == Material.ENCHANTED_BOOK || itemStack.getType() == Material.GOLD_BARDING || itemStack.getType() == Material.DIAMOND_BARDING)) {
                    entities.add(event.getEntity().getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack));
                }
            });
            event.getDrops().clear();

            profile.getMatch().getEntities().addAll(entities);
            profile.getMatch().handleDeath(event.getEntity(), event.getEntity().getKiller(), false);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(event.getPlayer().getLocation());

        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInFight()) {
            profile.getMatch().handleRespawn(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof ThrownPotion) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player shooter = (Player) event.getEntity().getShooter();
                Profile shooterData = Profile.getByUuid(shooter.getUniqueId());

                if (shooterData.isInFight() && shooterData.getMatch().isFighting()) {
                    shooterData.getMatch().getTeamPlayer(shooter).incrementPotionsThrown();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) event.getPotion().getShooter();
            Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
            if (shooterData.isSpectating()) {
                event.setCancelled(true);
            }

            if (shooterData.isInFight()) {
                if (event.getIntensity(shooter) <= 0.5D) {
                    shooterData.getMatch().getTeamPlayer(shooter).incrementPotionsMissed();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

                if (profile.isInFight()) {
                    if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                        if (!profile.getMatch().getKit().getGameRules().isHealthRegeneration()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    @Getter
    @RequiredArgsConstructor
    public static class WaterCheckTask extends BukkitRunnable {
        @Override
        public void run() {
            Player player = (Player) Bukkit.getOnlinePlayers();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            Match match = profile.getMatch();

            Bukkit.getOnlinePlayers().forEach(Player -> {

                if (profile != null && profile.getMatch().getState() != MatchState.FIGHTING) {
                    return;
                }

                Block legs = player.getLocation().getBlock();
                Block head = legs.getRelative(BlockFace.UP);
                if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                    match.onEnd();
                }
            });
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.isInFight()) {
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    profile.getMatch().handleDeath(player, null, false);
                    return;
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.LAVA && (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch())) {
                    if (profile.getMatch().getKit().getGameRules().isLavakill()) {
                        profile.getMatch().handleDeath(player, null, false);
                        return;
                    }
                }

                if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                    if (profile.getMatch().getKit().getGameRules().isParkour()) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (!profile.getMatch().isFighting()) {
                    event.setCancelled(true);
                    return;
                }

                if (profile.getMatch().isTeamMatch() || profile.getMatch().isHCFMatch()) {
                    if (!profile.getMatch().getTeamPlayer(player).isAlive()) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                    if (profile.getMatch().getKit().getGameRules().isSumo()) {
                        event.setDamage(0);
                        player.setHealth(20.0);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker;

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();
            } else {
                event.setCancelled(true);
                return;
            }
        } else {
            event.setCancelled(true);
            return;
        }


        if (attacker != null && event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
            Profile attackerProfile = Profile.getByUuid(attacker.getUniqueId());

            if (attackerProfile.isSpectating() || damagedProfile.isSpectating()) {
                event.setCancelled(true);
                return;
            }

            if (damagedProfile.isInFight() && attackerProfile.isInFight()) {
                Match match = attackerProfile.getMatch();

                if (!match.isHCFMatch() && !match.isKoTHMatch()) {
                    if (match.getKit().getGameRules().isSpleef()) {
                        if (!(event.getDamager() instanceof Projectile)) {
                            event.setCancelled(true);
                        }
                    }
                }

                if (!damagedProfile.getMatch().isHCFMatch() && !damagedProfile.getMatch().isKoTHMatch()) {
                    if (damagedProfile.getMatch().getKit().getGameRules().isSpleef() && (!(event.getDamager() instanceof Projectile))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (!damagedProfile.getMatch().getMatchId().equals(attackerProfile.getMatch().getMatchId())) {
                    event.setCancelled(true);
                    return;
                }

                if (!match.getTeamPlayer(damaged).isAlive() || !match.getTeamPlayer(attacker).isAlive() && !match.isFreeForAllMatch()) {
                    event.setCancelled(true);
                    return;
                }

                if (match.isSoloMatch() || match.isFreeForAllMatch()) {
                    attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
                    damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();

                    if (event.getDamager() instanceof Arrow) {
                        double range = Math.ceil(event.getEntity().getLocation().distance(attacker.getLocation()));
                        double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0D;

                        attacker.sendMessage(CC.translate("&c" + damaged.getName() + " is now at &c" + health + " &4" + StringEscapeUtils.unescapeJava("\u2764")));
                    }
                } else if (match.isTeamMatch() || match.isHCFMatch() || match.isKoTHMatch()) {
                    Team attackerTeam = match.getTeam(attacker);
                    Team damagedTeam = match.getTeam(damaged);

                    if (attackerTeam == null || damagedTeam == null) {
                        event.setCancelled(true);
                    } else {
                        if (attackerTeam.equals(damagedTeam)) {
                            event.setCancelled(true);
                        } else {
                            attackerProfile.getMatch().getTeamPlayer(attacker).handleHit();
                            damagedProfile.getMatch().getTeamPlayer(damaged).resetCombo();

                            if (event.getDamager() instanceof Arrow) {
                                double range = Math.ceil(event.getEntity().getLocation().distance(attacker.getLocation()));
                                double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0D;

                                attacker.sendMessage(CC.translate("&c" + damaged.getName() + " is now at &c" + health + " &4" + StringEscapeUtils.unescapeJava("\u2764")));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.getItem().getType().equals(Material.POTION)) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Array.get(), new Runnable() {
                public void run() {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }, 1L);
        }
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (event.getItem().hasItemMeta() &&
                    event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
                Player player = event.getPlayer();
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.isInFight()) {
                if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                    if (profile.getMatch() != null && profile.getMatch().getKit().getGameRules().isAntifoodloss()) {
                        event.setFoodLevel(20);
                    }
                }
            }

            if (profile.isInFight() && profile.getMatch().isFighting() && profile.getMatch().getKit().getGameRules().isAntifoodloss()) {
                if (event.getFoodLevel() >= 20) {
                    event.setFoodLevel(20);
                    player.setSaturation(20);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(PlayerItemConsumeEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInFight()) {
            if (profile.getMatch() != null && profile.getMatch().getKit() != null && profile.getMatch().getKit().getGameRules() != null) {
                if (profile.getMatch().getKit().getGameRules().isInfinitespeed()) {
                    if (event.getItem().getType() == Material.POTION) {
                        if (event.getItem().getDurability() == 8226) {
                            PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1);
                            player.addPotionEffect(effect, true);
                        }
                    }
                }
                if (profile.getMatch().getKit().getGameRules().isInfinitestrength()) {
                    if (event.getItem().getDurability() == 8233) {

                        PotionEffect effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1);
                        player.addPotionEffect(effect, true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

        if (profile.isInFight()) {
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        } else if (profile.isInMatch()) {
            profile.getMatch().handleDeath(event.getPlayer(), null, true);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
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
                Profile profile = Profile.getByUuid(shooter.getUniqueId());
                if (profile.isInFight()) {
                    if (!profile.getEnderpearlCooldown().hasExpired()) {
                        String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                        String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");
                        shooter.sendMessage(CC.RED + "You are on pearl cooldown for " + time + " " + context);
                        shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        event.setCancelled(true);
                    } else {
                        profile.setEnderpearlCooldown(new Cooldown(16_000));
                        profile.getMatch().onPearl(shooter, enderPearl);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportPearl(final PlayerTeleportEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (profile.isInFight()) {
                profile.getMatch().removePearl(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isSpectating()) {
            event.setCancelled(true);
        }
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {

            if (profile.isInFight()) {
                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT))) {
                        KitLoadout kitLoadout = profile.getMatch().getKit().getKitLoadout();
                        event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                        event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                        event.getPlayer().updateInventory();
                        event.setCancelled(true);
                        return;
                    } else if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT))) {
                        KitLoadout kitLoadout = Kit.getByName("HCFDIAMOND").getKitLoadout();
                        event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                        event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                        event.getPlayer().updateInventory();
                        Array.get().getArmorClassManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    } else if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.BARD_KIT))) {
                        KitLoadout kitLoadout = Kit.getByName("HCFBARD").getKitLoadout();
                        event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                        event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                        event.getPlayer().updateInventory();
                        Array.get().getArmorClassManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }else if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.ARCHER_KIT))) {
                        KitLoadout kitLoadout = Kit.getByName("HCFARCHER").getKitLoadout();
                        event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                        event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                        event.getPlayer().updateInventory();
                        Array.get().getArmorClassManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }else if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.ROGUE_KIT))) {
                        KitLoadout kitLoadout = Kit.getByName("HCFROGUE").getKitLoadout();
                        event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                        event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                        event.getPlayer().updateInventory();
                        Array.get().getArmorClassManager().attemptEquip(event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                }

                if (!profile.getMatch().isHCFMatch()) {
                    if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                        String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

                        if (displayName.startsWith("Kit: ")) {
                            String kitName = displayName.replace("Kit: ", "");

                            for (KitLoadout kitLoadout : profile.getKitData().get(profile.getMatch().getKit()).getLoadouts()) {
                                if (kitLoadout != null && ChatColor.stripColor(kitLoadout.getCustomName()).equals(kitName)) {
                                    event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                                    event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                                    event.getPlayer().updateInventory();
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                }

                Player player = event.getPlayer();
                if (((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) &&
                        (player.getItemInHand().getType() == Material.MUSHROOM_SOUP)) {
                    int health = (int) player.getHealth();
                    if ((health == 20)) {
                        player.getItemInHand().setType(Material.MUSHROOM_SOUP);
                    } else if (health >= 13) {
                        player.setHealth(20.0D);
                        player.getItemInHand().setType(Material.BOWL);
                    } else {
                        player.setHealth(health + 7);
                        player.getItemInHand().setType(Material.BOWL);
                    }
                }

                if (event.getItem().getType() == Material.ENDER_PEARL || (event.getItem().getType() == Material.POTION && event.getItem().getDurability() >= 16_000)) {
                    if (profile.isInFight() && profile.getMatch().isStarting()) {
                        event.setCancelled(true);
                        player.updateInventory();
                        return;
                    }
                }

                if (event.getItem().getType() == Material.ENDER_PEARL && event.getClickedBlock() == null) {
                    if (!profile.isInFight() || (profile.isInFight() && !profile.getMatch().isFighting())) {
                        event.setCancelled(true);
                        return;
                    }

                    if (profile.getMatch().isStarting()) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            if (e.getClickedBlock().getType() == Material.GOLD_PLATE) {
                Profile profile = Profile.getByUuid(e.getPlayer().getUniqueId());
                if (profile.isInFight()) {
                    if (!profile.getMatch().isHCFMatch() && !profile.getMatch().isKoTHMatch()) {
                        if (profile.getMatch().getKit().getGameRules().isParkour()) {
                            profile.getMatch().handleDeath(e.getPlayer(), null, false);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if ((e.isCancelled()) || (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            return;
        }
        Location target = e.getTo();
        target.setX(target.getBlockX() + 0.5D);
        target.setZ(target.getBlockZ() + 0.5D);
        e.setTo(target);
    }

    @EventHandler
    public void onKothMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            Profile profile = Profile.getByUuid(e.getPlayer().getUniqueId());
            if (profile.getMatch() != null && profile.getMatch().isKoTHMatch()) {
                Match match = profile.getMatch();
                if (match.getArena().getPoint().toCuboid() == null)
                    return;

                if (match.getArena().getPoint().toCuboid().isInCuboid(e.getPlayer())) {
                    if (match.getCapper() == null) {
                        if (!profile.isSpectating()) {
                            match.setCapper(e.getPlayer());
                        }
                    }
                } else {
                    if (match.getCapper() != null) {
                        if (match.getCapper().equals(e.getPlayer())) {
                            match.setCapper(null);
                            match.setTimer(20);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKothDeath(PlayerDeathEvent e) {
        Profile profile = Profile.getByUuid(e.getEntity().getUniqueId());
        if (profile.getMatch() != null && profile.getMatch().isKoTHMatch()) {
            Match match = profile.getMatch();
            if (match.getArena().getPoint().toCuboid() == null)
                return;

            if (match.getCapper() != null) {
                if (match.getCapper().equals(e.getEntity().getPlayer())) {
                    match.setCapper(null);
                    match.setTimer(20);
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        Iterator<LivingEntity> iterator = event.getAffectedEntities().iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();
            if (entity instanceof Player) {
                Player player = (Player) entity;

                Profile profile = Profile.getByUuid(player.getUniqueId());
                if (profile.isSpectating()) {
                    event.setIntensity(player, 0.0D);
                    iterator.remove();
                }
            }
        }
    }
}
