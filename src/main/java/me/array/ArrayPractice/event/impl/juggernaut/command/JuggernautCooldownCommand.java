package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "juggernaut cooldown", permission = "practice.juggernaut.cooldown")
public class JuggernautCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getJuggernautManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Juggernaut Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Juggernaut Event cooldown.");

		Array.get().getJuggernautManager().setCooldown(new Cooldown(0));
	}

}
