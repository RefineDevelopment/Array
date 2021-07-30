package xyz.refinedev.practice.profile;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.profile.SpawnTeleportEvent;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ProfileListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getClickedBlock() == null) {
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem().getType() == Material.PAINTING) {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (!e.getPlayer().hasPermission("array.build"))
                        e.setCancelled(true);
                }
            }

            if (e.getClickedBlock().getState() instanceof ItemFrame) {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (!e.getPlayer().hasPermission("array.build")) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isBusy() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            profile.refreshHotbar();
            profile.handleVisibility();
        }
            PlayerUtil.allowMovement(event.getPlayer());
        }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().hasPermission("array.build")) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().hasPermission("array.build")) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().hasPermission("array.build")) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().hasPermission("array.build")) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().hasPermission("array.build")) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemFrameItemRemoval(EntityDamageEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().hasPermission("array.build")) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().hasPermission("array.build")) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInLobby()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    profile.teleportToSpawn();
                }
            }
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);
            }
            if (profile.isInEvent() &&
                    (profile.getEvent().isSumoSolo()
                    || profile.getEvent().isSumoTeam()
                    || profile.getEvent().isGulagSolo()
                    || profile.getEvent().isGulagTeam()
                    || profile.getEvent().isSpleef()
                    || profile.getEvent().isParkour())) {
                event.setCancelled(true);
            }
            if (profile.isInSomeSortOfFight()) {
                if (profile.getMatch() != null && profile.getMatch().getState() == MatchState.STARTING) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Profile profile = new Profile(uuid);
        TaskUtil.runAsync(() -> {
            try {
                profile.load();
                Profile.getProfiles().put(uuid, profile);
                profile.handleJoin();
            } catch (Exception e) {
                e.printStackTrace();
                player.kickPlayer(CC.RED + "Failed to init your profile, Please contact an Administrator!");
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        profile.handleLeave();
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason() != null) {
            if (event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
                return;
            }
        }
        Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
        profile.handleLeave();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressurePlate(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
        }
    }

    /**
     * This is the patch for the Kit Editor Bug
     * when you can exit the editor with F6
     *
     * @param event {@link ProjectileLaunchEvent}
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onPearlThrow(final ProjectileLaunchEvent event) {
        final ProjectileSource source=event.getEntity().getShooter();
        if (source instanceof Player) {
            final Player shooter=(Player) source;
            final Profile profile=Profile.getByUuid(shooter.getUniqueId());
            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);
                profile.getKitEditor().setActive(false);
                PlayerUtil.reset(shooter);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        profile.refreshHotbar();
                    }
                }.runTaskLaterAsynchronously(Array.getInstance(), 2L);
            }
        }
    }

    /**
     * This is the patch for the Kit Editor Bug
     * when you can exit the editor with F6
     *
     * @param event {@link PotionSplashEvent}
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onPotionThrow(final PotionSplashEvent event) {
        ProjectileSource source = event.getPotion().getShooter();
            if (source instanceof Player) {
                final Player shooter=(Player) source;
                final Profile profile=Profile.getByUuid(shooter.getUniqueId());
                if (profile.isInLobby() || profile.isInQueue()) {
                    event.setCancelled(true);
                    profile.getKitEditor().setActive(false);
                    PlayerUtil.reset(shooter);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            profile.refreshHotbar();
                        }
                    }.runTaskLaterAsynchronously(Array.getInstance(), 2L);
                }
            }
    }

    /**
     * This is the patch for the Kit Editor Bug
     * when you can exit the editor with F6
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        final Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInLobby()) {
            if (profile.getKitEditor().isActive()) {
                event.setCancelled(true);
                profile.getKitEditor().setActive(false);
                PlayerUtil.reset(event.getPlayer());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        profile.refreshHotbar();
                    }
                }.runTaskLaterAsynchronously(Array.getInstance(), 2L);
            }
        }
    }

}
