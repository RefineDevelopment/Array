package me.array.ArrayPractice.util;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.AsyncCatcher;

public class PlayerUtil {

	public static void reset(Player player) {
		reset(player, true);
	}

	public static void reset(Player player, boolean resetHeldSlot) {
		AsyncCatcher.enabled = false;
		if (!player.hasMetadata("frozen")) {
			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.1F);
		}

		player.setHealth(20.0D);
		player.setSaturation(20.0F);
		player.setFallDistance(0.0F);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

		if (resetHeldSlot) {
			player.getInventory().setHeldItemSlot(0);
		}

		player.updateInventory();
	}

	public static int getPing(Player player) {
		int ping = ((CraftPlayer)player).getHandle().ping;

		return ping;
	}

	public static void spectator(Player player) {
		reset(player);

		player.setGameMode(GameMode.CREATIVE);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0.2F);
		player.updateInventory();
	}

	public static void denyMovement(Player player) {
		player.setWalkSpeed(0.0F);
		player.setFlySpeed(0.0F);
		player.setFoodLevel(0);
		player.setSprinting(false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
	}

	public static void allowMovement(Player player) {
		player.setWalkSpeed(0.2F);
		player.setFlySpeed(0.0001F);
		player.setFoodLevel(20);
		player.setSprinting(true);
		player.removePotionEffect(PotionEffectType.JUMP);
	}

	public static List<Player> convertUUIDListToPlayerList(List<UUID> list) {
		return list.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
	}

}
