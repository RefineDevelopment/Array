package me.drizzy.practice.hcf.bard.types;

import com.minexd.spigot.event.potion.PotionEffectExpireEvent;
import me.drizzy.practice.Array;
import me.drizzy.practice.hcf.bard.EffectRestorer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class SpigotX implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if ((livingEntity instanceof Player) && EffectRestorer.restores.containsRow(livingEntity.getUniqueId())) {
            final Player player = (Player) livingEntity;
            final PotionEffect previous = EffectRestorer.restores.get(player.getUniqueId(),
                    event.getEffect().getType());
            if (previous != null) {
                new BukkitRunnable() {
                    public void run() {
                        player.addPotionEffect(previous, true);
                        EffectRestorer.restores.remove(player.getUniqueId(), event.getEffect().getType());
                    }
                }.runTask(Array.getInstance());
            }
        }
    }
}
