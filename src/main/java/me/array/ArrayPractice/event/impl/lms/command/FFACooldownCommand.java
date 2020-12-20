package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "ffa cooldown", permission = "practice.staff")
public class FFACooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getFfaManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a FFA Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the FFA Event cooldown.");

		Array.get().getFfaManager().setCooldown(new Cooldown(0));
	}

}
