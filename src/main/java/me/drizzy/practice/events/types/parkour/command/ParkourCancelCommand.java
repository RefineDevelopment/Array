package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cancel", permission = "array.staff")
public class ParkourCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getParkourManager().getActiveParkour() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Parkour events.");
			return;
		}

		Profile.getProfiles().values().stream().filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
		Profile.getProfiles().values().stream().filter(Profile::isInQueue).forEach(Profile::refreshHotbar);
		Array.getInstance().getParkourManager().getActiveParkour().end(null);
	}

}
