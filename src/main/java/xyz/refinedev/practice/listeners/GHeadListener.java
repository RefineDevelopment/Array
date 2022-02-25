package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
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
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;

@RequiredArgsConstructor
public class GHeadListener implements Listener {

	private final Array plugin;

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event){
		Player player = event.getPlayer();
		Profile profile = plugin.getProfileManager().getProfile(player);

		if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) return;
		if (!event.getItem().getItemMeta().getDisplayName().toLowerCase().replace(" ", "").contains("goldenhead")) return;
		if (!event.getItem().getType().equals(Material.GOLDEN_APPLE)) return;

		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
		player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));

		if (!profile.isInFight()) return;
		Match match = profile.getMatch();

		if (match.isTheBridgeMatch()) {
			player.setHealth(event.getPlayer().getMaxHealth());
			this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> player.removePotionEffect(PotionEffectType.REGENERATION), 1L);
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
