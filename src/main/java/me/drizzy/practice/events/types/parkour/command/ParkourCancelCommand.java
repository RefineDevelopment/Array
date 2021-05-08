package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cancel", permission = "array.staff")
public class ParkourCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getParkourManager().getActiveParkour() == null) {
			sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
			return;
		}
		Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
		Array.getInstance().getParkourManager().getActiveParkour().end(null);
	}

}
