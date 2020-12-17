package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cooldown", permission = "practice.spleef.cooldown")
public class SpleefCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getSpleefManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Spleef Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Spleef Event cooldown.");

		Array.get().getSpleefManager().setCooldown(new Cooldown(0));
	}

}
