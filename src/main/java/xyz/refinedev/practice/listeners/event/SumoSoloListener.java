package xyz.refinedev.practice.listeners.event;

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
import org.bukkit.event.player.PlayerQuitEvent;
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

@RequiredArgsConstructor
public class SumoSoloListener implements Listener {
    
    private final Array plugin;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

        if (!profile.isInEvent() || !profile.getEvent().isSumoSolo()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

        if (!profile.isInEvent() || !profile.getEvent().isSumoSolo()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        Player player = ((Player) event.getEntity()).getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

        if (!profile.isInEvent() || !profile.getEvent().isSumoSolo()) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Event sumo = profile.getEvent();

        if (!profile.isInEvent() || !sumo.isSumoSolo()) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);

            if (!sumo.isFighting() || !sumo.isFighting(player.getUniqueId())) {
                player.teleport(plugin.getEventManager().getSpawn(sumo));
                return;
            }
            sumo.handleDeath(player);
            return;
        }

        if (!profile.getEvent().isFighting() || !profile.getEvent().isFighting(player.getUniqueId())) {
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
                event.setCancelled(true);
                return;
            }
            attacker = (Player) projectile.getShooter();
        }

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }

        if (attacker == null) return;

        Profile damagedProfile = plugin.getProfileManager().getByUUID(damaged.getUniqueId());
        Profile attackerProfile = plugin.getProfileManager().getByUUID(attacker.getUniqueId());

        if (!damagedProfile.isInEvent() || !damagedProfile.getEvent().isSumoSolo()) return;
        if (!attackerProfile.isInEvent() || !attackerProfile.getEvent().isSumoSolo()) return;

        Event sumo = damagedProfile.getEvent();

        if (!sumo.isFighting() || !sumo.isFighting(damaged.getUniqueId()) || !sumo.isFighting(attacker.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
