package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cooldown", permission = "practice.sumo.cooldown")
public class SumoCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSumoManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't any cooldown for the Sumo Event.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Sumo Event cooldown.");

		Array.getInstance().getSumoManager().setCooldown(new Cooldown(0));
	}

}
