package xyz.refinedev.practice.listeners;

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
import xyz.refinedev.practice.match.types.kit.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;

public class GHeadListener implements Listener {

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event){
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);

		if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) return;
		if (!event.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").contains("goldenhead")) return;
		if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) return;

		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
		player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));

		if (profile.isInFight()) {
			if (profile.getMatch() instanceof SoloBridgeMatch || profile.getMatch() instanceof TeamBridgeMatch) {
				player.setHealth(event.getPlayer().getMaxHealth());
			}
		}
	}

	public static ItemStack getGoldenHeadApple() {

		ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta itemMeta = is.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + "Golden Head");
		is.setItemMeta(itemMeta);

		return is;
	}
}
