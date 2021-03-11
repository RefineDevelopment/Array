package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo leave")
public class SumoLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Sumo activeSumo = Array.getInstance().getSumoManager().getActiveSumo();

		if (activeSumo == null) {
			player.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Sumo Event.");
			return;
		}

		Array.getInstance().getSumoManager().getActiveSumo().handleLeave(player);
	}

}
