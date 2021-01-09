package me.array.ArrayPractice.event.impl.parkour;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.event.impl.parkour.player.ParkourPlayerState;
import me.array.ArrayPractice.util.external.CC;
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

public class ParkourListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.isInParkour()) {
				event.setCancelled(true);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);
					if (profile.getParkour().isFighting(player)) {
						if (profile.getParkour().getEventPlayer(player).getLastLocation() != null) {
							player.teleport(profile.getParkour().getEventPlayer(player).getLastLocation());
						} else {
							player.teleport(Practice.getInstance().getParkourManager().getParkourSpawn());
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInParkour()) {
			profile.getParkour().handleLeave(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInParkour()) {
				if (profile.getParkour().getEventPlayer(event.getPlayer()).getState().equals(ParkourPlayerState.ELIMINATED)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onButton(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile.isInParkour()) {
			Parkour parkour = profile.getParkour();
			if (!parkour.getState().equals(ParkourState.ROUND_ENDING)) {
				if (event.getAction().equals(Action.PHYSICAL)) {
					if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
						if (parkour.getEventPlayer(player).getState().equals(ParkourPlayerState.WAITING)) {
							parkour.handleWin(event.getPlayer());
							parkour.broadcastMessage(CC.AQUA + event.getPlayer().getDisplayName() + CC.YELLOW + " has reached the end!");
						}
					} else if (event.getClickedBlock().getType() == Material.IRON_PLATE) {
						if (parkour.getEventPlayer(player).getState().equals(ParkourPlayerState.WAITING)) {
							parkour.getEventPlayer(event.getPlayer()).setLastLocation(event.getPlayer().getLocation());
							player.sendMessage(CC.translate("&b&l[Skywars] &aCheckpoint Acquired!"));
						}
					}
					event.setCancelled(true);
				}
			}
		}
	}
}
