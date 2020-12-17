package me.array.ArrayPractice.event.impl.skywars;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.event.impl.skywars.player.SkyWarsPlayerState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class SkyWarsListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
            if (profile.isInSkyWars()) {
                PlayerUtil.spectator(player);
                player.teleport(LocationUtil.deserialize(Array.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));
                if (player.getKiller() != null) {
                    profile.getSkyWars().handleDeath(player, player.getKiller());
                } else {
                    profile.getSkyWars().handleDeath(player, null);
                }
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
                SkyWars skyWars = Array.get().getSkyWarsManager().getActiveSkyWars();
                if (skyWars != null) {
                    if (skyWars.isWaiting() || skyWars.getState().equals(SkyWarsState.ROUND_ENDING)) {
                        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                            event.getEntity().teleport(LocationUtil.deserialize(Array.get().getSkyWarsManager().getSkyWarsSpectators().get(0)));
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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

            if (profile.isInSkyWars()) {
                if (profile.getSkyWars().getEventPlayer(event.getPlayer()).getState().equals(SkyWarsPlayerState.ELIMINATED)) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT))) {
                        KitLoadout kitLoadout = SkyWars.getKit().getKitLoadout();
                        event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
                        event.getPlayer().getInventory().setContents(kitLoadout.getContents());
                        event.getPlayer().updateInventory();
                        event.setCancelled(true);
                        return;
                    }
                }

                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

                    if (displayName.startsWith("Kit: ")) {
                        String kitName = displayName.replace("Kit: ", "");

                        for (KitLoadout kitLoadout : profile.getKitData().get(SkyWars.getKit()).getLoadouts()) {
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
            }
        }
    }
}
