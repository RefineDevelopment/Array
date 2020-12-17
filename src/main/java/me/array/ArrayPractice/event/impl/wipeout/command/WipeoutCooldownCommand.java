package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "wipeout cooldown", permission = "practice.wipeout.cooldown")
public class WipeoutCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getWipeoutManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Wipeout Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Wipeout Event cooldown.");

		Array.get().getWipeoutManager().setCooldown(new Cooldown(0));
	}

}
