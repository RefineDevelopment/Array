package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cooldown", permission = "practice.staff")
public class BracketsCooldownCommand {

	public void execute(CommandSender sender) {
		if (Practice.getInstance().getBracketsManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Brackets Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Brackets Event cooldown.");

		Practice.getInstance().getBracketsManager().setCooldown(new Cooldown(0));
	}

}
