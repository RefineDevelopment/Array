package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cooldown", permission = "practice.staff")
public class ParkourCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getParkourManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Parkour Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Parkour Event cooldown.");

		Array.get().getParkourManager().setCooldown(new Cooldown(0));
	}

}
