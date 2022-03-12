package xyz.refinedev.practice.util.timer.impl;

import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.TimeUtil;
import xyz.refinedev.practice.util.timer.PlayerTimer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnderpearlTimer extends PlayerTimer implements Listener {

	private final Array plugin;

	public EnderpearlTimer(Array plugin) {
		super("Enderpearl", TimeUnit.SECONDS.toMillis(plugin.getConfigHandler().getENDERPEARL_COOLDOWN()));

		this.plugin = plugin;
	}

	@Override
	protected void handleExpiry(Player player, UUID playerUUID) {
		super.handleExpiry(player, playerUUID);

		if (player == null) {
			return;
		}

		Profile profile = plugin.getProfileManager().getProfile(playerUUID);
		if (profile.isInSomeSortOfFight()) player.sendMessage(Locale.MATCH_PEARL_COOLDOWN_EXPIRE.toString());
	}

	@EventHandler
	public void onPearlLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			if (event.getEntity() instanceof EnderPearl) {
				Player player = (Player) event.getEntity().getShooter();
				Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
				if (!profile.isInSomeSortOfFight()) {
					this.clearCooldown(player);
					return;
				}
				long cooldown = this.getRemaining(player);
				if (cooldown > 0) {
					event.setCancelled(true);
					String time = TimeUtil.millisToSeconds(cooldown);
					String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");

					player.sendMessage(Locale.MATCH_PEARL_COOLDOWN.toString().replace("<cooldown>", time + " " + context));
					player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
					player.updateInventory();
					return;
				}
				this.setCooldown(player, player.getUniqueId());
				if (plugin.getServer().getPluginManager().isPluginEnabled("LunarClient-API"))
					LunarClientAPICooldown.sendCooldown(player, "Enderpearl");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		}
		Player player = event.getPlayer();
		if (player.hasPotionEffect(PotionEffectType.WEAKNESS) || player.hasMetadata("denyMove")) {
			event.setCancelled(true);
			return;
		}

		if (this.getRemaining(player) != 0L && event.isCancelled()) {
			this.clearCooldown(player);
		}
	}
}
