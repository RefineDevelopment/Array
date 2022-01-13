package xyz.refinedev.practice.listeners.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.parkour.Parkour;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayerState;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ParkourListener implements Listener {

    private final Array plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        Parkour parkour = (Parkour) profileEvent;
        EventPlayer eventPlayer = parkour.getEventPlayer(player.getUniqueId());

        event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.getEntity().setFireTicks(0);

            if (!parkour.isFighting(player.getUniqueId())) return;
            if (eventPlayer.getLastLocation() != null) {
                player.teleport(eventPlayer.getLastLocation());
            } else {
                player.teleport(this.plugin.getEventManager().getHelper().getSpawn(parkour));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        Parkour parkour = (Parkour) profileEvent;
        if (parkour.isFighting(player.getUniqueId())) return;

        event.setCancelled(true);
    }
    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        HumanEntity player = event.getWhoClicked();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        Parkour parkour = (Parkour) profileEvent;
        if (parkour.isFighting(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        event.setCancelled(true);
    }


    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!event.getAction().name().contains("RIGHT")) return;

        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        Parkour parkour = (Parkour) profileEvent;
        EventPlayer eventPlayer = parkour.getEventPlayer(player.getUniqueId());

        if (eventPlayer.getState().equals(EventPlayerState.ELIMINATED)) event.setCancelled(true);
    }

    @EventHandler
    public void onButton(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if (event.getClickedBlock() == null) return;
        if (!profile.isInEvent()) return;

        Event profileEvent = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!profileEvent.isParkour()) return;

        if (profileEvent.getState().equals(EventState.ROUND_ENDING)) return;

        Parkour parkour = (Parkour) profileEvent;
        EventPlayer eventPlayer = profileEvent.getEventPlayer(player.getUniqueId());

        if (!eventPlayer.getState().equals(EventPlayerState.WAITING)) return;
        if (!action.equals(Action.PHYSICAL)) return;
        if (profile.getParkourCheckpoints().contains(block.getLocation())) return;

        switch (block.getType()) {
            case GOLD_PLATE: {
                parkour.handleWin(player);
                parkour.broadcastMessage(Locale.EVENT_WON.toString().replace("<winner>", player.getDisplayName()));
                profile.getParkourCheckpoints().add(event.getClickedBlock().getLocation());
                break;
            }
            case IRON_PLATE: {
                eventPlayer.setLastLocation(player.getLocation());
                player.sendMessage(Locale.MATCH_CHECKPOINT.toString());
                profile.getParkourCheckpoints().add(event.getClickedBlock().getLocation());
                break;
            }
        }
    }
}
