package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cancel", permission = "practice.staff")
public class ParkourCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getParkourManager().getActiveParkour() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Parkour event.");
			return;
		}

		Array.get().getParkourManager().getActiveParkour().end(null);
	}

}
