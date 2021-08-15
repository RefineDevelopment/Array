package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.brackets.BracketsState;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.events.types.sumo.SumoState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo join")
public class SumoJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Sumo activeSumo = Array.getInstance().getSumoManager().getActiveSumo();

		if (profile.isBusy() || profile.getParty() != null) {
			player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
			return;
		}

		if (activeSumo == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
			return;
		}

		if (activeSumo.getState() != SumoState.WAITING) {
			player.sendMessage(Locale.EVENT_ALREADY_STARED.toString().replace("<event>", "Sumo"));
			return;
		}
		activeSumo.handleJoin(player);
	}

}
