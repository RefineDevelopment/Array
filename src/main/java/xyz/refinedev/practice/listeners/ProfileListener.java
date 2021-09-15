package xyz.refinedev.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.profile.SpawnTeleportEvent;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.UUID;

public class ProfileListener implements Listener {

    private final Array plugin = Array.getInstance();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getClickedBlock() == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();


        if (item != null) {
            if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
                if (item.getType().equals(Material.PAINTING) || item.getType().equals(Material.TRAP_DOOR)) {
                    event.setCancelled(true);
                }
            }
        }

        if (block != null) {
            if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
                if (block.getState() instanceof ItemFrame) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isBusy()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        profile.refreshHotbar();
        profile.handleVisibility();
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemFrameItemRemoval(EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("array.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.isInLobby()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.isInLobby() && !profile.isInQueue()) return;

        event.setCancelled(true);
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            profile.teleportToSpawn();
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInLobby() || profile.isInQueue()) {
            event.setCancelled(true);
        }

        if (profile.isInEvent()) {
            Event profileEvent = profile.getEvent();
            if (profileEvent.isBracketsSolo() || profileEvent.isBracketsTeam() || profileEvent.isLMS()) return;

            event.setCancelled(true);
        }

        if (profile.isInMatch()) {
            if (profile.getMatch().isStarting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void AsyncPlayerLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getConfigHandler().isLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(CC.RED + "The server is still loading, please wait for it to load!");
            return;
        }

        UUID uuid = event.getUniqueId();
        Profile profile = new Profile(uuid);

        try {
            profile.load();
        } catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(CC.RED + "Failed to init your profile, Please contact an Administrator!");
        }
        Profile.getProfiles().put(uuid, profile);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        profile.handleJoin();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInEvent()) {
            Event profileEvent = profile.getEvent();
            profileEvent.handleLeave(player);
        }

        TaskUtil.runAsync(profile::handleLeave);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason() != null) {
            if (event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
                return;
            }
        }
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        TaskUtil.runAsync(profile::handleLeave);
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
    public void onPearlThrow(ProjectileLaunchEvent event) {
        final ProjectileSource source = event.getEntity().getShooter();
        if (source instanceof Player) {
            final Player shooter = (Player) source;
            final Profile profile = Profile.getByUuid(shooter.getUniqueId());
            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);
                profile.getKitEditor().setActive(false);
                PlayerUtil.reset(shooter);
                TaskUtil.runLaterAsync(profile::refreshHotbar, 2L);
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
    public void onPotionThrow(PotionSplashEvent event) {
        ProjectileSource source = event.getPotion().getShooter();
            if (source instanceof Player) {
                final Player shooter=(Player) source;
                final Profile profile=Profile.getByUuid(shooter.getUniqueId());
                if (profile.isInLobby() || profile.isInQueue()) {
                    event.setCancelled(true);
                    profile.getKitEditor().setActive(false);
                    PlayerUtil.reset(shooter);
                    TaskUtil.runLaterAsync(profile::refreshHotbar, 2L);
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
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.isInLobby() || !profile.getKitEditor().isActive()) return;

        event.setCancelled(true);
        profile.getKitEditor().setActive(false);
        PlayerUtil.reset(event.getPlayer());
        TaskUtil.runLaterAsync(profile::refreshHotbar, 2L);
    }

}
