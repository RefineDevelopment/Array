package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "infected cooldown", permission = "practice.infected.cooldown")
public class InfectedCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getInfectedManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Infected Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Infected Event cooldown.");

		Array.get().getInfectedManager().setCooldown(new Cooldown(0));
	}

}
