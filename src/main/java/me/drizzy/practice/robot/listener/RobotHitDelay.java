package me.drizzy.practice.robot.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Drizzy
 * Created at 5/4/2021
 */

public class RobotHitDelay implements Listener {

	private final HashMap<UUID, Long> times = new HashMap<>();

	@EventHandler(ignoreCancelled = true,priority = EventPriority.NORMAL)
	public void onDamage(EntityDamageEvent e) {
		UUID uuid = e.getEntity().getUniqueId();

		if (e.getEntity().hasMetadata("array-bot")) {
			if (times.containsKey(uuid)) {
				long l = times.get(uuid);
				long minHitDelay=490;
				if (System.currentTimeMillis() - l < minHitDelay) {
					e.setCancelled(true);
					return;
				}
			}
			times.put(uuid, System.currentTimeMillis());
		}
	}
}
