package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "gulag cooldown", permission = "array.staff")
public class GulagCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getGulagManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.translate("&7There is no currently active Gulag Event cooldown."));
			return;
		}

		sender.sendMessage(CC.translate("&7Successfully reset the &cGulag Event &7cooldown."));
		Array.getInstance().getGulagManager().setCooldown(new Cooldown(0));
	}

}
