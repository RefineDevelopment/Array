package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit list", "kits"}, permission = "practice.dev")
public class KitListCommand {

	public void execute(Player player) {
		player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
		player.sendMessage(CC.translate("&bArray &7» All Kits"));
		player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
		for ( Kit kit : Kit.getKits() ) {
			if (kit == null) {
				player.sendMessage(CC.translate(""));
				player.sendMessage(CC.translate("&7&oThere are no kits"));
				player.sendMessage(CC.translate(""));
			} else {
				player.sendMessage(CC.translate("&7» &b" + kit.getName() + (kit.isEnabled() ? " &7[&aEnabled&7] " : " &7[&cDisabled&7] ") + (kit.getGameRules().isRanked() ? "&7[&aRanked&7]" : "&7[&eNot-Ranked&7]")));
			}
		}
		player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
	}
}
