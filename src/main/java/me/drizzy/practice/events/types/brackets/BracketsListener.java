package me.drizzy.practice.events.types.brackets;

import me.drizzy.practice.Locale;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.Array;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.Cooldown;
import me.drizzy.practice.util.other.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class BracketsListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
			if (profile.isInBrackets()) {
				event.getDrops().clear();
				profile.getBrackets().handleDeath(player);
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
				if (profile.isInBrackets()) {
					if (profile.getBrackets().getState().equals(BracketsState.ROUND_STARTING)) {
						event.setCancelled(true);
						return;
					}

					if (!profile.getEnderpearlCooldown().hasExpired()) {
						String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
						String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");
						shooter.sendMessage(Locale.MATCH_PEARL_COOLDOWN.toString().replace("<cooldown>", time + " " + context));
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

			if (damagedProfile.isInBrackets() && attackerProfile.isInBrackets()) {
				Brackets brackets = damagedProfile.getBrackets();

				if (!brackets.isFighting() || !brackets.isFighting(damaged.getUniqueId()) ||
						!brackets.isFighting(attacker.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInBrackets()) {
			if (!profile.getBrackets().isFighting(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
		}

		if (profile.isInBrackets() && profile.getBrackets().isFighting()) {
			if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
				event.getItemDrop().remove();
				return;
			}

			profile.getBrackets().getEntities().add(event.getItemDrop());
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInBrackets()) {
			if (!profile.getBrackets().isFighting(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
			if (profile.isInBrackets() && !profile.getBrackets().isFighting(event.getEntity().getUniqueId())) {
				if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInBrackets()) {
			profile.getBrackets().handleLeave(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInBrackets()) {
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					if (event.getItem().equals(Hotbar.getItems().get(HotbarType.DEFAULT_KIT))) {
						KitInventory kitInventory= profile.getBrackets().getKit().getKitInventory();
						event.getPlayer().getInventory().setArmorContents(kitInventory.getArmor());
						event.getPlayer().getInventory().setContents(kitInventory.getContents());
						event.getPlayer().updateInventory();
						event.setCancelled(true);
						return;
					}
				}

				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					final String displayName = CC.translate(event.getItem().getItemMeta().getDisplayName());
					if (displayName.endsWith(" (Right-Click)")) {
						final String kitName = displayName.replace(" (Right-Click)", "");
						for ( final KitInventory kitInventory2 : profile.getStatisticsData().get(profile.getMatch().getKit()).getLoadouts() ) {
							if (kitInventory2 != null && ChatColor.stripColor(kitInventory2.getCustomName()).equals(ChatColor.stripColor(kitName))) {
								event.getPlayer().getInventory().setArmorContents(kitInventory2.getArmor());
								event.getPlayer().getInventory().setContents(kitInventory2.getContents());
								event.getPlayer().getActivePotionEffects().clear();
								event.getPlayer().addPotionEffects(profile.getMatch().getKit().getKitInventory().getEffects());
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

				if (event.getItem().getType() == Material.ENDER_PEARL) {
					if (profile.getBrackets().getState().equals(BracketsState.ROUND_STARTING)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		final Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInBrackets()) {
			Brackets brackets = profile.getBrackets();
				if (brackets.getKit().getGameRules().isBuild() && profile.getBrackets().isFighting(event.getPlayer().getUniqueId())) {
					if (brackets.getKit().getGameRules().isSpleef()) {
						event.setCancelled(true);
						return;
					}
					int y = (int) event.getBlockPlaced().getLocation().getY();
					if (y > brackets.getMaxBuildHeight()) {
						event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
						event.setCancelled(true);
						return;
					}
					brackets.getPlacedBlocks().add(event.getBlock().getLocation());
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void onBreak(BlockBreakEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInBrackets()) {
			Brackets brackets=profile.getBrackets();
			if (brackets.getKit().getGameRules().isBuild() && profile.getBrackets().isFighting(event.getPlayer().getUniqueId())) {
				if (brackets.getKit().getGameRules().isSpleef()) {
					if (brackets.getPlacedBlocks().remove(event.getBlock().getLocation())) {
						event.getPlayer().getInventory().addItem(new ItemBuilder(event.getBlock().getType()).durability(event.getBlock().getData()).build());
						event.getPlayer().updateInventory();
						event.getBlock().setType(Material.AIR);
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}


}
