package xyz.refinedev.practice.events.types.lms;

import xyz.refinedev.practice.events.types.lms.player.LMSPlayerState;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.TimeUtil;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class LMSListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {
        Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
        if (profile.isInLMS()) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (player.getKiller() != null) {
                    profile.getLms().handleDeath(player, player.getKiller());
                } else {
                    profile.getLms().handleDeath(player, null);
                }
                event.getDrops().clear();
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
                if (profile.isInLMS()) {
                    if (profile.getLms().getState().equals(LMSState.ROUND_STARTING)) {
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

            if (damagedProfile.isInLMS() && attackerProfile.isInLMS()) {
                LMS LMS = damagedProfile.getLms();

                if (!LMS.isFighting() || !LMS.isFighting(damaged) || !LMS.isFighting(attacker)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInLMS()) {
            profile.getLms().handleLeave(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isInLMS()) {
            if (!profile.getLms().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        if (profile.isInLMS()) {
            if (!profile.getLms().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInLMS()) {
            if (!profile.getLms().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInLMS()) {
            if (!profile.getLms().isFighting(profile.getPlayer())) {
                event.setCancelled(true);
            }
        }  else if (profile.getLms() != null && profile.getLms().getSpectators().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            Profile profile=Profile.getByPlayer(player);
            if (profile.isInLMS() && profile.getLms().isWaiting()) {
                event.setCancelled(true);
            } else if (profile.getLms() != null && profile.getLms().getSpectators().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

            if (profile.isInLMS()) {
                if (profile.getLms().getEventPlayer(event.getPlayer()).getState().equals(LMSPlayerState.ELIMINATED)) {
                    event.setCancelled(true);
                    return;
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInLMS()) {
            LMS lms = profile.getLms();
            if (lms.getKit().getGameRules().isBuild() && profile.getLms().isFighting(event.getPlayer())) {
                if (lms.getKit().getGameRules().isSpleef()) {
                    event.setCancelled(true);
                    return;
                }
                int y = (int) event.getBlockPlaced().getLocation().getY();
                if (y > lms.getMaxBuildHeight()) {
                    event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
                    event.setCancelled(true);
                    return;
                }
                lms.getPlacedBlocks().add(event.getBlock().getLocation());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInLMS()) {
            LMS lms = profile.getLms();
            if (lms.getKit().getGameRules().isBuild() && profile.getLms().isFighting(event.getPlayer())) {
                if (lms.getKit().getGameRules().isSpleef()) {
                    if (lms.getPlacedBlocks().remove(event.getBlock().getLocation())) {
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
}
