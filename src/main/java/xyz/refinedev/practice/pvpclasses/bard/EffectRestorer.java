package xyz.refinedev.practice.pvpclasses.bard;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.pvpclasses.classes.Bard;
import xyz.refinedev.practice.pvpclasses.events.ArmorClassUnequipEvent;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class EffectRestorer implements Listener {

    private final Array plugin;
    public static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public void init() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorClassUnequip(ArmorClassUnequipEvent event) {
        restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        restores.rowKeySet().remove(event.getEntity().getUniqueId());
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
}