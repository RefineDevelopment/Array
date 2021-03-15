package me.drizzy.practice.event.types.oitc;

import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.event.types.oitc.player.OITCPlayerState;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class OITCListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {
        Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
        if (profile.isInOITC()) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                PlayerUtil.spectator(player);
                if (player.getKiller() != null) {
                    profile.getOITC().handleDeath(player, player.getKiller());
                } else {
                    profile.getOITC().handleDeath(player, null);
                }
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
                if (profile.isInOITC()) {
                    if (profile.getOITC().getState().equals(OITCState.ROUND_STARTING)) {
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

            if (damagedProfile.isInOITC() && attackerProfile.isInOITC()) {
                OITC OITC = damagedProfile.getOITC();

                if (!OITC.isFighting() || !OITC.isFighting(damaged) || !OITC.isFighting(attacker)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInOITC()) {
            profile.getOITC().handleLeave(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isInOITC()) {
            if (!profile.getOITC().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isInOITC()) {
            if (!profile.getOITC().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInOITC()) {
            if (!profile.getOITC().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInOITC()) {
            if (!profile.getOITC().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

            if (profile.isInOITC()) {
                if (profile.getOITC().getEventPlayer(event.getPlayer()).getState().equals(OITCPlayerState.ELIMINATED)) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    if (event.getItem().equals(Hotbar.getItems().get(HotbarType.DEFAULT_KIT))) {
                        KitInventory kitInventory= profile.getOITC().getKit().getKitInventory();
                        event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                        event.getPlayer().getInventory().setContents(kitInventory.getContents());
                        event.getPlayer().getActivePotionEffects().clear();
                        event.getPlayer().addPotionEffects(profile.getOITC().getKit().getKitInventory().getEffects());
                        event.getPlayer().updateInventory();
                        event.setCancelled(true);
                        return;
                    }
                }

                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

                    if (displayName.startsWith("Kit: ")) {
                        String kitName = displayName.replace("Kit: ", "");

                        for ( KitInventory kitInventory : profile.getStatisticsData().get(profile.getOITC().getKit()).getLoadouts()) {
                            if (kitInventory != null && ChatColor.stripColor(kitInventory.getCustomName()).equals(kitName)) {
                                event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
                                event.getPlayer().getInventory().setContents(kitInventory.getContents());
                                event.getPlayer().getActivePotionEffects().clear();
                                event.getPlayer().addPotionEffects(profile.getOITC().getKit().getKitInventory().getEffects());
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
