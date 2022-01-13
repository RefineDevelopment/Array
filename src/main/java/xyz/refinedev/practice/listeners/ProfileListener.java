package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.TrapDoor;
import org.bukkit.projectiles.ProjectileSource;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;

import java.util.UUID;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final Array plugin;

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0];
        Player player = event.getPlayer();
        Profile profile = this.plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (profile.getKitEditor().isRenaming()) {
            event.getPlayer().sendMessage(ChatColor.RED + "A kit name cannot start with \"/\".");
            event.getPlayer().sendMessage(ChatColor.RED + "Event cancelled.");
            profile.getKitEditor().setActive(false);
            profile.getKitEditor().setRename(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getClickedBlock() == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        Profile profile = this.plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if ((player.getGameMode() == GameMode.CREATIVE && !profile.isSpectating()) || profile.isBuild()) return;
        if (event.getAction().name().endsWith("_BLOCK")
                && (event.getClickedBlock().getType().name().contains("FENCE")
                && event.getClickedBlock().getState() instanceof TrapDoor
                || event.getClickedBlock().getType().name().contains("TRAP")
                || event.getClickedBlock().getType().name().contains("CHEST")
                || event.getClickedBlock().getType().name().contains("DOOR")
                || event.getClickedBlock().getType().equals(Material.BEACON)
                || event.getClickedBlock().getType().equals(Material.FURNACE)
                || event.getClickedBlock().getType().equals(Material.WORKBENCH)
                || event.getClickedBlock().getType().equals(Material.NOTE_BLOCK)
                || event.getClickedBlock().getType().equals(Material.JUKEBOX)
                || event.getClickedBlock().getType().equals(Material.ANVIL)
                || event.getClickedBlock().getType().equals(Material.HOPPER)
                || event.getClickedBlock().getType().equals(Material.BED_BLOCK)
                || event.getClickedBlock().getType().equals(Material.DROPPER)
                || event.getClickedBlock().getType().equals(Material.ITEM_FRAME)
                || event.getClickedBlock().getType().equals(Material.BREWING_STAND))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE && !profile.isBuild()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE && !profile.isBuild()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE && !profile.isBuild()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE && !profile.isBuild()) {
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
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (profile.isInSomeSortOfFight()) return;

        if (player.getGameMode() != GameMode.CREATIVE && !profile.isBuild()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInLobby()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInLobby() && !profile.isInQueue()) return;

        event.setCancelled(true);
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            plugin.getProfileManager().teleportToSpawn(profile);
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (profile.isInLobby() || profile.isInQueue()) {
            event.setCancelled(true);
        }

        if (profile.isInEvent()) {
            Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
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
            plugin.getProfileManager().load(profile);
        } catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(CC.RED + "Failed to load your profile, Please contact an Administrator!");
            return;
        }
        plugin.getProfileManager().getProfiles().put(uuid, profile);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);

        plugin.getProfileManager().handleJoin(profile);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);

        if (profile.isInEvent()) {
            Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
            profileEvent.handleLeave(player);
        }

        plugin.getProfileManager().handleLeave(profile);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKickEvent(PlayerKickEvent event) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(event.getPlayer().getUniqueId());
        plugin.getProfileManager().handleLeave(profile);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressurePlate(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        event.getPlayer().setSprinting(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer == null) return;

        Profile profile = plugin.getProfileManager().getProfileByUUID(killer.getUniqueId());
        if (!profile.isInFight() && !profile.isInEvent()) return;

        profile.setKills(profile.getKills() + 1);

        if (profile.isInFight()) {
            Match match = profile.getMatch();
            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            if (teamPlayer == null || !teamPlayer.isAlive() || teamPlayer.isDisconnected()) return;

            teamPlayer.setKills(teamPlayer.getKills() + 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInFight() && !profile.isInEvent()) return;

        profile.setDeaths(profile.getDeaths() + 1);
    }

    /**
     * This is the patch for the Kit Editor Bug
     * when you can exit the editor with F6
     *
     * @param event {@link ProjectileLaunchEvent}
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onPearlThrow(ProjectileLaunchEvent event) {
        ProjectileSource source = event.getEntity().getShooter();
        if (!(source instanceof Player)) return;

        Player shooter = (Player) source;
        Profile profile = plugin.getProfileManager().getProfileByUUID(shooter.getUniqueId());
        if (!profile.isInLobby() && !profile.isInQueue()) return;

        event.setCancelled(true);
        profile.getKitEditor().setActive(false);
        plugin.getProfileManager().refreshHotbar(profile);
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
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInLobby() || !profile.getKitEditor().isActive()) return;

        event.setCancelled(true);
        profile.getKitEditor().setActive(false);
        plugin.getProfileManager().refreshHotbar(profile);
    }

}
