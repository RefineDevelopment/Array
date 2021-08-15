package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cancel", permission = "array.staff")
public class SumoCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSumoManager().getActiveSumo() == null) {
			sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
			return;
		}
		Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
		Array.getInstance().getSumoManager().getActiveSumo().end();
	}

}
