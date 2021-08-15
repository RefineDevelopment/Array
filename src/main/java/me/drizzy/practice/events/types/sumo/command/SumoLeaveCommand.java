package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.sumo.Sumo;
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
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
			return;
		}

		if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Sumo"));
			return;
		}

		Array.getInstance().getSumoManager().getActiveSumo().handleLeave(player);
	}

}
