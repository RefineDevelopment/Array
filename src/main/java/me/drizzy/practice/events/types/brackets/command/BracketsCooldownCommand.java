package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cooldown", permission = "array.staff")
public class BracketsCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getBracketsManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Brackets Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Brackets Event cooldown.");

		Array.getInstance().getBracketsManager().setCooldown(new Cooldown(0));
	}

}
