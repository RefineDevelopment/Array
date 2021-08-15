package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit list", "kits"}, permission = "array.dev")
public class KitListCommand {

	public void execute(CommandSender player) {
		player.sendMessage(CC.CHAT_BAR);
		player.sendMessage(CC.translate("&cArray &7» All Kits"));
		player.sendMessage(CC.CHAT_BAR);
		for ( Kit kit : Kit.getKits() ) {
			if (kit == null) {
				player.sendMessage(CC.translate(""));
				player.sendMessage(CC.translate("&7&oThere are no kits setup."));
				player.sendMessage(CC.translate(""));
			} else {
				player.sendMessage(CC.translate(" • " + (kit.isEnabled() ? CC.GREEN : CC.RED) + kit.getName() + (kit.getGameRules().isRanked() ? " &7[&aRanked&7]" : " &7[&eNot-Ranked&7]")));
			}
		}
		player.sendMessage(CC.CHAT_BAR);
	}
}
