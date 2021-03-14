package me.drizzy.practice.event.types.sumo;

import me.drizzy.practice.Array;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerMoveEvent;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.BlockUtil;
import me.drizzy.practice.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SumoListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInSumo()) {
			if (!profile.getSumo().isFighting(event.getPlayer().getUniqueId())) {
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

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent e) {
		Player player=e.getPlayer();
		Location to=e.getTo();
		Location from=e.getFrom();
		Profile profile = Profile.getByUuid(player);
		Sumo sumo = profile.getSumo();

		if (profile.getMatch() !=null && profile.getMatch().isSumoMatch() || profile.getMatch() !=null && profile.getMatch().isSumoTeamMatch()) {
			if (BlockUtil.isOnLiquid(e.getFrom(), 1)) {
				profile.getMatch().handleDeath(player, profile.getMatch().getOpponentPlayer(player), false);
			}
		}

		if (sumo == null) {
			return;
		}

		if (profile.isInSumo()) {
			if (sumo.getState() == SumoState.ROUND_FIGHTING) {
				if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
					sumo.handleDeath(player);
				}
			}
		}

		if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
			if (sumo.getState() == SumoState.ROUND_STARTING) {
				player.teleport(from);
				((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
			}
		}

	}

}
