package xyz.refinedev.practice.listeners.event.sumo;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

//TODO: Clean this up
@RequiredArgsConstructor
public class SumoTeamListener implements Listener {
    
    private final Array plugin;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInEvent()) return;

        Event sumo = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!sumo.isSumoTeam()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInEvent()) return;

        Event sumo = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!sumo.isSumoTeam()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        Player player = ((Player) event.getEntity()).getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInEvent()) return;

        Event sumo = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!sumo.isSumoTeam()) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInEvent()) return;

        Event sumo = plugin.getEventManager().getEventByUUID(profile.getEvent());
        if (!sumo.isSumoTeam()) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);

            if (!sumo.isFighting() || !sumo.isFighting(player.getUniqueId())) {
                player.teleport(plugin.getEventManager().getHelper().getSpawn(sumo));
                return;
            }
            sumo.handleDeath(player);
            return;
        }

        if (!sumo.isFighting() || !sumo.isFighting(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0);
        player.setHealth(20.0);
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Player attacker = null;

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }
            attacker = (Player) projectile.getShooter();
        }

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }

        if (attacker == null) return;

        Profile damagedProfile = plugin.getProfileManager().getProfileByUUID(damaged.getUniqueId());
        Profile attackerProfile = plugin.getProfileManager().getProfileByUUID(attacker.getUniqueId());

        if (!damagedProfile.isInEvent() || !attackerProfile.isInEvent()) return;

        Event damagedEvent = plugin.getEventManager().getEventByUUID(damagedProfile.getEvent());
        Event attackerEvent = plugin.getEventManager().getEventByUUID(attackerProfile.getEvent());

        if (!damagedEvent.isSumoTeam() || !attackerEvent.isSumoTeam()) return;
        if (!damagedEvent.getEventId().equals(attackerEvent.getEventId())) return;
        if (damagedEvent.isFighting() && damagedEvent.isFighting(damaged.getUniqueId()) && damagedEvent.isFighting(attacker.getUniqueId())) return;

        event.setCancelled(true);
    }
}
