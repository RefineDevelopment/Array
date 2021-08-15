package me.drizzy.practice.events.types.sumo;

import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.sumo.player.SumoPlayer;
import me.drizzy.practice.match.team.TeamPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.location.BlockUtil;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class SumoListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		Player player = event.getPlayer();
		if (profile.isInSumo()) {
			if (!profile.getSumo().isFighting(player.getUniqueId())) {
				event.setCancelled(true);
			}
		} else if (profile.getSumo() != null && profile.getSumo().getSpectators().contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInSumo() || (profile.getSumo() != null && profile.getSumo().getSpectators().contains(event.getPlayer().getUniqueId()))) {
			if (profile.isInSumo()) {
				if (!profile.getSumo().isFighting(player.getUniqueId())) {
					event.setCancelled(true);
				}
			} else if (profile.getSumo() != null && profile.getSumo().getSpectators().contains(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void onHit(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			Player player = ((Player) event.getEntity()).getPlayer();
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.isInSumo()) {
				if (!profile.getSumo().isFighting(player.getUniqueId())) {
					event.setCancelled(true);
				}
			} else if (profile.getSumo() != null && profile.getSumo().getSpectators().contains(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.isInSumo()) {
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);
					if (!profile.getSumo().isFighting() || !profile.getSumo().isFighting(player.getUniqueId())) {
						player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());
						return;
					}  else if (profile.getSumo() != null && profile.getSumo().getSpectators().contains(player.getUniqueId())) {
						event.setCancelled(true);
						return;
					}
					PlayerUtil.spectator(player);
					player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());
					profile.getSumo().handleDeath(player);
					return;
				}

				if (profile.getSumo() != null) {
					if (!profile.getSumo().isFighting() || !profile.getSumo().isFighting(player.getUniqueId())) {
						event.setCancelled(true);
						return;
					} else if (profile.getSumo() != null && profile.getSumo().getSpectators().contains(player.getUniqueId())) {
						event.setCancelled(true);
					}

					event.setDamage(0);
					player.setHealth(20.0);
					player.updateInventory();
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

			if (damagedProfile.isInSumo() && attackerProfile.isInSumo()) {
				Sumo sumo = damagedProfile.getSumo();

				if (!sumo.isFighting() || !sumo.isFighting(damaged.getUniqueId()) ||
				    !sumo.isFighting(attacker.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInSumo()) {
			profile.getSumo().handleLeave(event.getPlayer());
		}
	}
}
