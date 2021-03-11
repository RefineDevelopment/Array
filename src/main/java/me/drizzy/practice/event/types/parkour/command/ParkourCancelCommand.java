package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cancel", permission = "array.staff")
public class ParkourCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getParkourManager().getActiveParkour() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Parkour event.");
			return;
		}

		Array.getInstance().getParkourManager().getActiveParkour().end(null);
	}

}
