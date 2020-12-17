package me.array.ArrayPractice.event.impl.juggernaut;

import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.event.impl.juggernaut.player.JuggernautPlayerState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class JuggernautListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onDeath(EntityDeathEvent event) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();

				Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
				if (profile.isInJuggernaut()) {
				PlayerUtil.spectator(player);
				player.teleport(Array.get().getJuggernautManager().getJuggernautSpectator());
				if (player.getKiller() != null) {
					profile.getJuggernaut().handleDeath(player, player.getKiller());
				} else {
					profile.getJuggernaut().handleDeath(player, null);
				}
			}
		}
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile instanceof EnderPearl) {
			EnderPearl enderPearl = (EnderPearl) projectile;
			ProjectileSource source = enderPearl.getShooter();
			if (source instanceof Player) {
				Player shooter = (Player) source;
				Profile profile = Profile.getByUuid(shooter.getUniqueId());
				if (profile.isInJuggernaut()) {
					if (profile.getJuggernaut().getState().equals(JuggernautState.ROUND_STARTING)) {
						event.setCancelled(true);
						return;
					}

					if (!profile.getEnderpearlCooldown().hasExpired()) {
						String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
						String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");
						shooter.sendMessage(CC.RED + "You are on pearl cooldown for " + time + " " + context);
						shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
						event.setCancelled(true);
					} else {
						profile.setEnderpearlCooldown(new Cooldown(16_000));
					}
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

			if (damagedProfile.isInJuggernaut() && attackerProfile.isInJuggernaut()) {
				Juggernaut juggernaut = damagedProfile.getJuggernaut();

				if (!juggernaut.getEventPlayer(damaged).isJuggernaut() && !juggernaut.getEventPlayer(attacker).isJuggernaut()) {
					event.setCancelled(true);
				}

				if (!juggernaut.isFighting(damaged) || !juggernaut.isFighting(attacker)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInJuggernaut()) {
			profile.getJuggernaut().handleLeave(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInJuggernaut()) {
			if (!profile.getJuggernaut().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInJuggernaut()) {
			if (!profile.getJuggernaut().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInJuggernaut()) {
			if (!profile.getJuggernaut().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInJuggernaut()) {
			if (!profile.getJuggernaut().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInJuggernaut()) {
				if (profile.getJuggernaut().getEventPlayer(event.getPlayer()).getState().equals(JuggernautPlayerState.ELIMINATED)) {
					event.setCancelled(true);
					return;
				}
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT))) {
						KitLoadout kitLoadout = profile.getJuggernaut().getEventPlayer(event.getPlayer()).getKit().getKitLoadout();
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

						for (KitLoadout kitLoadout : profile.getKitData().get(profile.getJuggernaut().getEventPlayer(event.getPlayer()).getKit()).getLoadouts()) {
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
