package me.drizzy.practice.hcf.bard;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.drizzy.practice.hcf.events.ArmorClassUnequipEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.Array;
import me.drizzy.practice.hcf.classes.Bard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pt.foxspigot.jar.event.potion.PotionEffectExpireEvent;

import java.util.Collection;
import java.util.UUID;

public class EffectRestorer implements Listener {

    private final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public EffectRestorer(Array plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorClassUnequip(ArmorClassUnequipEvent event) {
        restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    public void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) continue;

            // If the current potion effect has a higher amplifier, ignore this one.
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            } else if (effect.getAmplifier() == active.getAmplifier()) {
                // If the current potion effect has a longer duration, ignore this one.
                if (effect.getDuration() < active.getDuration()) {
                    return;
                }
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }

        // Cancel the previous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > Bard.HELD_EFFECT_DURATION_TICKS && effect.getDuration() < Bard.DEFAULT_MAX_DURATION) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if ((livingEntity instanceof Player) && this.restores.containsRow(livingEntity.getUniqueId())) {
            final Player player = (Player) livingEntity;
            final PotionEffect previous = this.restores.get(player.getUniqueId(),
                    event.getEffect().getType());
            if (previous != null) {
                new BukkitRunnable() {
                    public void run() {
                        player.addPotionEffect(previous, true);
                        EffectRestorer.this.restores.remove(player.getUniqueId(), event.getEffect().getType());
                    }
                }.runTask(Array.getInstance());
            }
        }
    }
}