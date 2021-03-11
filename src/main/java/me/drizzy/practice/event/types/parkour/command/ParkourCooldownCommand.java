package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cooldown", permission = "array.staff")
public class ParkourCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getParkourManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Parkour Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Parkour Event cooldown.");

		Array.getInstance().getParkourManager().setCooldown(new Cooldown(0));
	}

}
