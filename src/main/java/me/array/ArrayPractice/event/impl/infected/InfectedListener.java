package me.array.ArrayPractice.event.impl.infected;

import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import me.array.ArrayPractice.event.impl.infected.player.InfectedPlayerState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InfectedListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
			if (profile.isInInfected()) {
				Player player = (Player) event.getEntity();
				if (player.getKiller() != null) {
					profile.getInfected().handleDeath(player, player.getKiller(), false);
				} else {
					profile.getInfected().handleDeath(player, null, false);
				}
				event.getDrops().clear();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
				Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

				if (profile.isInInfected()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
			if (profile.isInInfected()) {
				if (profile.getInfected().getState().equals(InfectedState.WAITING)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Player attacker;

		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
				attacker = (Player) ((Projectile) event.getDamager()).getShooter();
			} else {
				event.setCancelled(true);
				return;
			}
		} else {
			event.setCancelled(true);
			return;
		}

		if (attacker != null && event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
			Profile attackerProfile = Profile.getByUuid(attacker.getUniqueId());

			if (damagedProfile.isInInfected() && attackerProfile.isInInfected()) {
				Infected infected = damagedProfile.getInfected();

				if (!infected.getEventPlayer(damaged).isInfected() && !infected.getEventPlayer(attacker).isInfected()) {
					event.setCancelled(true);
				}

				if (infected.getEventPlayer(damaged).isInfected() && infected.getEventPlayer(attacker).isInfected()) {
					event.setCancelled(true);
				}

				if (!infected.isFighting(damaged) || !infected.isFighting(attacker)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInInfected()) {
			profile.getInfected().handleLeave(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInInfected()) {
			if (!profile.getInfected().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInInfected()) {
			if (!profile.getInfected().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInInfected()) {
			if (!profile.getInfected().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInInfected()) {
			if (!profile.getInfected().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInInfected()) {
				if (profile.getInfected().getEventPlayer(event.getPlayer()).getState().equals(InfectedPlayerState.ELIMINATED)) {
					event.setCancelled(true);
					return;
				}
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT))) {
						KitLoadout kitLoadout = profile.getInfected().getEventPlayer(event.getPlayer()).getKit().getKitLoadout();
						event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
						event.getPlayer().getInventory().setContents(kitLoadout.getContents());
						event.getPlayer().updateInventory();
						event.setCancelled(true);
						return;
					}
				}

				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

					if (displayName.startsWith("Kit: ")) {
						String kitName = displayName.replace("Kit: ", "");

						for (KitLoadout kitLoadout : profile.getKitData().get(profile.getInfected().getEventPlayer(event.getPlayer()).getKit()).getLoadouts()) {
							if (kitLoadout != null && ChatColor.stripColor(kitLoadout.getCustomName()).equals(kitName)) {
								event.getPlayer().getInventory().setArmorContents(kitLoadout.getArmor());
								event.getPlayer().getInventory().setContents(kitLoadout.getContents());
								event.getPlayer().updateInventory();
								event.setCancelled(true);
								return;
							}
						}
					}
				}

				Player player = event.getPlayer();
				if (((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) &&
						(player.getItemInHand().getType() == Material.MUSHROOM_SOUP))
				{
					int health = (int)player.getHealth();
					if ((health == 20)) {
						player.getItemInHand().setType(Material.MUSHROOM_SOUP);
					} else if (health >= 13) {
						player.setHealth(20.0D);
						player.getItemInHand().setType(Material.BOWL);
					} else {
						player.setHealth(health + 7);
						player.getItemInHand().setType(Material.BOWL);
					}
				}
			}
		}
	}
}
