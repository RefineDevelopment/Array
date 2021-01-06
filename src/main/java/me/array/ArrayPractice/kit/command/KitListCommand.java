package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit list", "kits"}, permission = "practice.staff")
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
				player.sendMessage(CC.translate("&7» &b" + kit.getName() + (kit.isEnabled() ? " &7[&aEnabled&7]" : " &7[&cDisabled&7]")));
			}
			player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
		}
	}
}
