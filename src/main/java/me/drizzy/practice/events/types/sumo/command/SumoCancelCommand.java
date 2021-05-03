package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cancel", permission = "array.staff")
public class SumoCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSumoManager().getActiveSumo() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		Profile.getProfiles().values().stream().filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
		Profile.getProfiles().values().stream().filter(Profile::isInQueue).forEach(Profile::refreshHotbar);
		Array.getInstance().getSumoManager().getActiveSumo().end();
	}

}
