package me.drizzy.practice.event.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "gulag cooldown", permission = "array.staff")
public class GulagCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getGulagManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Gulag Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Gulag Event cooldown.");

		Array.getInstance().getGulagManager().setCooldown(new Cooldown(0));
	}

}
