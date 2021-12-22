package xyz.refinedev.practice.listeners.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventHelperUtil;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.spleef.Spleef;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayerState;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/13/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SpleefListener implements Listener {

    private final Array plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Event hostEvent = this.plugin.getEventManager().getEvent(player.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;

        Spleef spleef = (Spleef) hostEvent;
        EventPlayer eventPlayer = spleef.getEventPlayer(player.getUniqueId());


        if (!eventPlayer.getState().equals(EventPlayerState.WAITING) || !spleef.getState().equals(EventState.ROUND_FIGHTING)) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() != Material.SNOW_BLOCK || event.getBlock().getType() != Material.SNOW ) {
            event.setCancelled(true);
            return;
        }

        spleef.getChangedBlocks().add(block.getState());
        block.setType(Material.AIR);

        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Event hostEvent = this.plugin.getEventManager().getEvent(player.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;

        Spleef spleef = (Spleef) hostEvent;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID && event.getCause() != EntityDamageEvent.DamageCause.LAVA) return;

        event.setCancelled(true);
        player.setFireTicks(0);

        if (!spleef.isFighting() || !spleef.isFighting(player.getUniqueId())) {
            player.teleport(EventHelperUtil.getSpawn(spleef));
            return;
        }

        spleef.handleDeath(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() == null || !(event.getDamager() instanceof Player)) return;
        Player attacker = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        Event damagedProfile = plugin.getEventManager().getEvent(damaged.getUniqueId());
        Event attackerProfile = plugin.getEventManager().getEvent(attacker.getUniqueId());

        if (damagedProfile == null || !damagedProfile.isSpleef()) return;
        if (attackerProfile == null || !attackerProfile.isSpleef()) return;

        Spleef spleef = (Spleef) damagedProfile;

        if (spleef.isFighting(damaged.getUniqueId()) && spleef.isFighting(attacker.getUniqueId())) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Event hostEvent = this.plugin.getEventManager().getEvent(player.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;
        if (!hostEvent.isFighting(player.getUniqueId())) return;

        event.setCancelled(true);
    }
    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        HumanEntity player = event.getWhoClicked();
        Event hostEvent = this.plugin.getEventManager().getEvent(player.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;
        if (!hostEvent.isFighting(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Event hostEvent = this.plugin.getEventManager().getEvent(player.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;

        Spleef spleef = (Spleef) hostEvent;

        if (spleef.isFighting(player.getUniqueId())) {
            spleef.getDroppedItems().add(event.getItemDrop());
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Event hostEvent = this.plugin.getEventManager().getEvent(player.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;

        Spleef spleef = (Spleef) hostEvent;

        if (spleef.isFighting(player.getUniqueId())) {
            spleef.getDroppedItems().remove(event.getItem());
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof  Player)) return;
        Player shooter = (Player) event.getEntity().getShooter();

        Profile shooterProfile = this.plugin.getProfileManager().getByUUID(shooter.getUniqueId());
        Event hostEvent = this.plugin.getEventManager().getEvent(shooter.getUniqueId());

        if (hostEvent == null) return;
        if (!hostEvent.isSpleef()) return;
        if (!hostEvent.isFighting(shooter.getUniqueId())) return;

        if (event.getEntity() instanceof Arrow) {
            shooterProfile.getMatch().getEntities().add(event.getEntity());
            return;
        }

        if (event.getEntity() instanceof Snowball) {
            BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0, 4);
            if (!iterator.hasNext()) return;
            Block hitBlock = iterator.next();

            if (hitBlock.getType() == Material.SNOW_BLOCK) {
                hostEvent.getChangedBlocks().add(hitBlock.getState());
                hitBlock.setType(Material.AIR);
            }

            shooter.playSound(shooter.getLocation(), Sound.CHICKEN_EGG_POP, 20F, 1F);
            shooter.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
            shooter.updateInventory();
        }
    }
}
