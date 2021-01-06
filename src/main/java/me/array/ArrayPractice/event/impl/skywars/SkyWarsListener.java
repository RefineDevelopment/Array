package me.array.ArrayPractice.event.impl.skywars;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.skywars.player.SkyWarsPlayerState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.util.external.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class SkyWarsListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {
        Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
        if (profile.isInSkyWars()) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                PlayerUtil.spectator(player);
                player.teleport(LocationUtil.deserialize(Practice.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));
                if (player.getKiller() != null) {
                    profile.getSkyWars().handleDeath(player, player.getKiller());
                } else {
                    profile.getSkyWars().handleDeath(player, null);
                }

                Bukkit.getScheduler().runTaskLaterAsynchronously(Practice.get(), () -> event.getDrops().clear(), 10 * 20);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSkyWars()) {
            SkyWars skyWars = profile.getSkyWars();
            if (skyWars.getEventPlayer(event.getPlayer()).getState().equals(SkyWarsPlayerState.WAITING)) {
                skyWars.getPlacedBlocks().add(event.getBlock().getLocation());
            } else {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSkyWars()) {
            SkyWars skyWars = profile.getSkyWars();
            if (skyWars.getEventPlayer(event.getPlayer()).getState().equals(SkyWarsPlayerState.WAITING)) {
                if (!skyWars.getPlacedBlocks().remove(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLiquidPlace(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSkyWars()) {
            SkyWars skyWars = profile.getSkyWars();
            if (skyWars.getEventPlayer(event.getPlayer()).getState().equals(SkyWarsPlayerState.WAITING)) {
                Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                skyWars.getPlacedBlocks().add(block.getLocation());
            } else {
                event.setCancelled(true);
            }
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
                if (profile.isInSkyWars()) {
                    if (profile.getSkyWars().getState().equals(SkyWarsState.ROUND_STARTING)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (!profile.getEnderpearlCooldown().hasExpired()) {
                        String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                        String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");
                        shooter.sendMessage(CC.RED + "You are on pearl cooldown for " + time + " " + context);
                        shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        event.setCancelled(true);
                    } else {
                        profile.setEnderpearlCooldown(new Cooldown(16_000));
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
            if (profile.isInSkyWars()) {
                SkyWars skyWars = Practice.get().getSkyWarsManager().getActiveSkyWars();
                if (skyWars != null) {
                    if (skyWars.isWaiting() || skyWars.getState().equals(SkyWarsState.ROUND_ENDING)) {
                        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                            event.getEntity().teleport(LocationUtil.deserialize(Practice.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));
                        }
                        event.setCancelled(true);
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

            if (damagedProfile.isInSkyWars() && attackerProfile.isInSkyWars()) {
                SkyWars skyWars = damagedProfile.getSkyWars();

                if (!skyWars.isFighting() || !skyWars.isFighting(damaged) || !skyWars.isFighting(attacker)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSkyWars()) {
            profile.getSkyWars().handleLeave(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isInSkyWars()) {
            if (!profile.getSkyWars().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isInSkyWars()) {
            if (!profile.getSkyWars().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInSkyWars()) {
            if (!profile.getSkyWars().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInSkyWars()) {
            if (!profile.getSkyWars().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

}
