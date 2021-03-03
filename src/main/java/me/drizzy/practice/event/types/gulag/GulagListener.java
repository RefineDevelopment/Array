package me.drizzy.practice.event.types.gulag;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.KitLoadout;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.hotbar.HotbarItem;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.FireworkEffectPlayer;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GulagListener implements Listener {

	private final FireworkEffectPlayer fireworkEffectPlayer=new FireworkEffectPlayer();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onDeath(EntityDeathEvent event) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				Profile profile = Profile.getProfiles().get(event.getEntity().getUniqueId());
				if (profile.isInGulag()) {
				profile.getGulag().handleDeath(player);
				player.teleport(Array.getInstance().getGulagManager().getGulagSpectator());
				event.getDrops().clear();
				event.getEntity().setFireTicks(0);
				PlayerUtil.reset(player);
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
				if (profile.isInGulag()) {
					if (profile.getGulag().getState().equals(GulagState.ROUND_STARTING)) {
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

			if (damagedProfile.isInGulag() && attackerProfile.isInGulag()) {
				if (!(event.getDamager() instanceof Projectile)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInGulag()) {
			if (!profile.getGulag().isFighting(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
		}

		if (profile.isInGulag() && profile.getGulag().isFighting()) {
			if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
				event.getItemDrop().remove();
				return;
			}

			profile.getGulag().getEntities().add(event.getItemDrop());
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInGulag()) {
			if (!profile.getGulag().isFighting(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
			if (profile.isInGulag()) {
				if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInGulag()) {
			profile.getGulag().handleLeave(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInGulag()) {
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
					if (event.getItem().equals(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT))) {
						KitLoadout kitLoadout = profile.getGulag().getKit().getKitLoadout();
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

						for (KitLoadout kitLoadout : profile.getKitData().get(profile.getGulag().getKit()).getLoadouts()) {
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

				if (event.getItem().getType() == Material.ENDER_PEARL) {
					if (profile.getGulag().getState().equals(GulagState.ROUND_STARTING)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player =event.getPlayer();
		if (!event.hasItem() || event.getItem().getType() != Material.DIAMOND_HOE || !event.getAction().name().contains("RIGHT_") || event.getItem().getItemMeta() != Hotbar.getItems().get(HotbarItem.GULAG_EVENT).getItemMeta()) {
			return;
		}
		final FireworkEffect effect=FireworkEffect.builder().withColor(Color.BLUE).with(FireworkEffect.Type.BALL_LARGE).build();
		final Snowball snowball=player.launchProjectile(Snowball.class);
		snowball.setVelocity(snowball.getVelocity().multiply(2));
		new BukkitRunnable() {
			int ticks=0;
			public void run() {
				if (this.ticks++ >= 100) {
					this.cancel();
					return;
				}
				if (snowball.isDead() || snowball.isOnGround()) {
					for ( Entity entity : snowball.getNearbyEntities(4.0, 4.0, 4.0) ) {
						entity.setVelocity(entity.getLocation().toVector().subtract(snowball.getLocation().toVector()).normalize().add(new Vector(0.0, 0.7, 0.0)));
					}
					snowball.remove();
					this.cancel();
				} else {
					try {
						GulagListener.this.fireworkEffectPlayer.playFirework(snowball.getWorld(), snowball.getLocation(), effect);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}.runTaskTimer(Array.getInstance(), 1L, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onHit(EntityDamageByEntityEvent event) {
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

			if (damagedProfile.isInGulag() && attackerProfile.isInGulag()) {
				damaged.playSound(damaged.getLocation(), Sound.EXPLODE, 20F, 15F);
				damaged.setHealth(damaged.getHealthScale() - 0.8);
			}
		}
	}
}
