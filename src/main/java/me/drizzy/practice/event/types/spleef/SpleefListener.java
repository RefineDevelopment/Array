package me.drizzy.practice.event.types.spleef;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.spleef.player.SpleefPlayerState;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class SpleefListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onBreak(BlockBreakEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInSpleef()) {
			Spleef spleef = profile.getSpleef();
			if (spleef.getEventPlayer(event.getPlayer()).getState().equals(SpleefPlayerState.WAITING) && spleef.getState().equals(SpleefState.ROUND_FIGHTING)) {
				if (event.getBlock().getType() == Material.SNOW_BLOCK ||
						event.getBlock().getType() == Material.SNOW) {
					spleef.getChangedBlocks().add(event.getBlock().getState());

					event.getBlock().setType(Material.AIR);
					event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
					event.getPlayer().updateInventory();
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.isInSpleef()) {
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);
					if (!profile.getSpleef().isFighting() || !profile.getSpleef().isFighting(player)) {
						player.teleport(Array.getInstance().getSpleefManager().getSpleefSpectator());
						return;
					}
					PlayerUtil.spectator(player);
					player.teleport(Array.getInstance().getSpleefManager().getSpleefSpectator());
					profile.getSpleef().handleDeath(player);
					return;
				}
				if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
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

			if (attacker != null && event.getEntity() instanceof Player) {
				Player damaged = (Player) event.getEntity();
				Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
				Profile attackerProfile = Profile.getByUuid(attacker.getUniqueId());

				if (damagedProfile.isInSpleef() && attackerProfile.isInSpleef()) {
					Spleef spleef = damagedProfile.getSpleef();
					if (spleef.isFighting(damaged) && spleef.isFighting(attacker)) {
						if (!event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInSpleef()) {
			profile.getSpleef().handleLeave(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInSpleef()) {
			if (!profile.getSpleef().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInSpleef()) {
			if (!profile.getSpleef().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInSpleef()) {
			if (!profile.getSpleef().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInSpleef()) {
			if (!profile.getSpleef().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInSpleef()) {
				if (profile.getSpleef().getEventPlayer(event.getPlayer()).getState().equals(SpleefPlayerState.ELIMINATED)) {
					event.setCancelled(true);
					return;
				}
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					if (event.getItem().equals(Hotbar.getItems().get(HotbarType.DEFAULT_KIT))) {
						KitInventory kitInventory= Spleef.getKit().getKitInventory();
						event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
						event.getPlayer().getInventory().setContents(kitInventory.getContents());
						event.getPlayer().updateInventory();
						event.setCancelled(true);
						return;
					}
				}

				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

					if (displayName.startsWith("Kit: ")) {
						String kitName = displayName.replace("Kit: ", "");

						for ( KitInventory kitInventory : profile.getStatisticsData().get(Spleef.getKit()).getLoadouts()) {
							if (kitInventory != null && ChatColor.stripColor(kitInventory.getCustomName()).equals(kitName)) {
								event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
								event.getPlayer().getInventory().setContents(kitInventory.getContents());
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
