

package me.array.ArrayPractice.match.kits.utils.bard;

import java.util.Collection;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.match.kits.Bard;
import me.array.ArrayPractice.match.kits.utils.events.ArmorClassUnequipEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.google.common.collect.HashBasedTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.UUID;
import com.google.common.collect.Table;
import org.bukkit.event.Listener;

public class EffectRestorer implements Listener {
    private Table<UUID, PotionEffectType, PotionEffect> restores=HashBasedTable.create();

    public EffectRestorer(Array plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onArmorClassUnequip(final ArmorClassUnequipEvent event) {
        this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    public void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel=true;
        Collection<PotionEffect> activeList=player.getActivePotionEffects();
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
            shouldCancel=false;
            break;
        }
        // Cancel the previous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > Bard.HELD_EFFECT_DURATION_TICKS && effect.getDuration() < Bard.DEFAULT_MAX_DURATION) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }
}
