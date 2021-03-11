package me.drizzy.practice.array.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoldenHeads implements Listener {

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event){
		Player player = event.getPlayer();
		if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) return;
		if(event.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").contains("goldenhead")) {
			if (event.getItem().getType() == Material.GOLDEN_APPLE){
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*90, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*9, 1), true);
			}
		}
		if(event.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").replace("'", "").contains("adamsapple")) {
			if (event.getItem().getType() == Material.GOLDEN_APPLE){
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*90, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*9, 0), true);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				player.setLevel(0);
				player.setExp(0f);
				player.setFireTicks(0);
			}
		}

	}
	
	public static ItemStack goldenHeadItem() {
			ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
			ItemMeta itemMeta = is.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GOLD + "Golden Head");
			is.setItemMeta(itemMeta);
			return is;
	}

	public static ItemStack getBridgeApple() {
		ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta itemMeta = is.getItemMeta();
		itemMeta.setDisplayName(ChatColor.AQUA + "Adam's Apple");
		is.setItemMeta(itemMeta);
		return is;
	}
}
