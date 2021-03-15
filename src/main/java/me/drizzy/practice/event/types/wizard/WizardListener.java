package me.drizzy.practice.event.types.wizard;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.FireworkEffectPlayer;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class WizardListener implements Listener {

	private final FireworkEffectPlayer fireworkEffectPlayer=new FireworkEffectPlayer();

	@EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player=(Player) event.getEntity();
			Profile profile=Profile.getByUuid(event.getEntity().getUniqueId());
			if (profile.isInWizard()) {
				profile.getWizard().handleDeath(player);
				player.teleport(Array.getInstance().getWizardManager().getWizardSpectator());
				event.getDrops().clear();
				event.getEntity().setFireTicks(0);
				PlayerUtil.reset(player);
			}
		}
	}


	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile=event.getEntity();
		if (projectile instanceof EnderPearl) {
			EnderPearl enderPearl=(EnderPearl) projectile;
			ProjectileSource source=enderPearl.getShooter();
			if (source instanceof Player) {
				Player shooter=(Player) source;
				Profile profile=Profile.getByUuid(shooter.getUniqueId());
				if (profile.isInWizard()) {
					if (profile.getWizard().getState().equals(WizardState.ROUND_STARTING)) {
						event.setCancelled(true);
						return;
					}

					if (!profile.getEnderpearlCooldown().hasExpired()) {
						String time=TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
						String context="second" + (time.equalsIgnoreCase("1.0") ? "" : "s");
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

	@EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (Profile.getByUuid(event.getEntity().getUniqueId()).isInWizard()) {
			Player attacker;

			if (event.getDamager() instanceof Player) {
				attacker=(Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile) {
				if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
					attacker=(Player) ((Projectile) event.getDamager()).getShooter();
				} else {
					event.setCancelled(true);
					return;
				}
			} else {
				event.setCancelled(true);
				return;
			}

			if (attacker != null && event.getEntity() instanceof Player) {
				Player damaged=(Player) event.getEntity();
				Profile damagedProfile=Profile.getByUuid(damaged.getUniqueId());
				Profile attackerProfile=Profile.getByUuid(attacker.getUniqueId());

				if (damagedProfile.isInWizard() && attackerProfile.isInWizard()) {
					if (!(event.getDamager() instanceof Projectile)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInWizard()) {
			if (!profile.getWizard().isFighting(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
		}

		if (profile.isInWizard() && profile.getWizard().isFighting()) {
			if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
				event.getItemDrop().remove();
				return;
			}

			profile.getWizard().getEntities().add(event.getItemDrop());
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInWizard()) {
			if (!profile.getWizard().isFighting(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile=Profile.getByUuid(event.getEntity().getUniqueId());
			if (profile.isInWizard()) {
				if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInWizard()) {
			profile.getWizard().handleLeave(event.getPlayer());
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player=event.getPlayer();
		if (Profile.getByUuid(player).isInWizard()) {
			if (!event.hasItem() || event.getItem().getType() != Material.STICK || !event.getAction().name().contains("RIGHT_")) {
				return;
			}
			Profile profile = Profile.getByUuid(player);
			if (!profile.getWizardReloadCooldown().hasExpired()) {
				player.sendMessage(CC.RED + "Recharging wand!");
				event.setCancelled(true);
				return;
			} else {
				profile.setWizardCooldown(new Cooldown(TimeUtil.parseTime("2s")));
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
							WizardListener.this.fireworkEffectPlayer.playFirework(snowball.getWorld(), snowball.getLocation(), effect);
						} catch (Exception ex) {
							//
						}
					}
				}
			}.runTaskTimer(Array.getInstance(), 1L, 1L);
		}
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
	public void onHit(EntityDamageEvent event) {
		Player attacker;
		if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
			attacker=(Player) event.getEntity();
			if (attacker != null) {
				Profile profile=Profile.getByUuid(attacker);
				if (profile.isInWizard()) {
					if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
						event.setDamage(3.0);

					} else {
						event.setCancelled(true);
						return;
					}
					if (event.getEntity() instanceof Player) {
						Player damaged=(Player) event.getEntity();
						Profile damagedProfile=Profile.getByUuid(damaged.getUniqueId());
						Profile attackerProfile=Profile.getByUuid(attacker.getUniqueId());

						if (damagedProfile.isInWizard() && attackerProfile.isInWizard()) {
							Vector launchingLocation = attacker.getLocation().getDirection(); // 100% coded by veltus
							damaged.setVelocity(launchingLocation.multiply(10));
							damaged.playSound(damaged.getLocation(), Sound.EXPLODE, 20F, 15F);
							damaged.getWorld().spigot().playEffect(damaged.getLocation(), Effect.SMOKE,
									26, 0, 0.2F, 0.5F, 0.2F, 0.2F, 12, 387);
						}
					}
				}
			}
		}
	}
}
