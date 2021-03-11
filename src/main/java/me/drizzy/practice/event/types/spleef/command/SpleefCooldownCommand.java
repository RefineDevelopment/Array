package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cooldown", permission = "array.staff")
public class SpleefCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSpleefManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Spleef Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Spleef Event cooldown.");

		Array.getInstance().getSpleefManager().setCooldown(new Cooldown(0));
	}

}
