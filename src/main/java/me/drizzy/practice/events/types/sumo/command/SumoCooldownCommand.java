package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.Cooldown;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cooldown", permission = "array.staff")
public class SumoCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSumoManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.translate("&7There is no currently active Sumo Event cooldown."));
			return;
		}
		sender.sendMessage(CC.translate("&7Successfully reset the &cSumo Event &7cooldown."));
		Array.getInstance().getSumoManager().setCooldown(new Cooldown(0));
	}

}
