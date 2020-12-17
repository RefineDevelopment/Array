package me.array.ArrayPractice.event.impl.wipeout;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.event.impl.wipeout.player.WipeoutPlayerState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

public class WipeoutListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.isInWipeout()) {
				event.setCancelled(true);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);
					if (profile.getWipeout().isFighting(player)) {
						if (profile.getWipeout().getEventPlayer(player).getLastLocation() != null) {
							player.teleport(profile.getWipeout().getEventPlayer(player).getLastLocation());
						} else {
							player.teleport(Array.get().getWipeoutManager().getWipeoutSpawn());
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInWipeout()) {
			profile.getWipeout().handleLeave(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInWipeout()) {
			if (!profile.getWipeout().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInWipeout()) {
			if (!profile.getWipeout().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInWipeout()) {
			if (!profile.getWipeout().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInWipeout()) {
			if (!profile.getWipeout().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onButton(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile.isInWipeout()) {
			Wipeout wipeout = profile.getWipeout();
			if (!wipeout.getState().equals(WipeoutState.ROUND_ENDING)) {
				if (event.getAction().equals(Action.PHYSICAL)) {
					if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
						if (wipeout.getEventPlayer(player).getState().equals(WipeoutPlayerState.WAITING)) {
							wipeout.handleWin(event.getPlayer());
						}
					} else if (event.getClickedBlock().getType() == Material.IRON_PLATE) {
						if (wipeout.getEventPlayer(player).getState().equals(WipeoutPlayerState.WAITING)) {
							wipeout.getEventPlayer(event.getPlayer()).setLastLocation(event.getPlayer().getLocation());
						}
					}
				}
			}
		}
	}
}
