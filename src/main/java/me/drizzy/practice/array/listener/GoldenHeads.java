package me.drizzy.practice.array.listener;

import me.drizzy.practice.util.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoldenHeads implements Listener {

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e){
		Player p = e.getPlayer();
		if (e.getItem() == null || !e.getItem().hasItemMeta()
				|| !e.getItem().getItemMeta().hasDisplayName()) return;
		if(e.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").contains("goldenhead")) {
			if (e.getItem().getType() == Material.GOLDEN_APPLE){
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*90, 0), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*9, 1), true);
			}
		}
	}
	
	public static ItemStack goldenHeadItem() {
		return ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GOLD + "Golden Head");
	}
}
