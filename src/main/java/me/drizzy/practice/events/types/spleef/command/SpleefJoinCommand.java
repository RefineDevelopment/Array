package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.spleef.SpleefState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef join")
public class SpleefJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Spleef activeSpleef = Array.getInstance().getSpleefManager().getActiveSpleef();

		if (profile.isBusy() || profile.getParty() != null) {
			player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
			return;
		}

		if (activeSpleef == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
			return;
		}

		if (activeSpleef.getState() != SpleefState.WAITING) {
			player.sendMessage(Locale.EVENT_ALREADY_STARED.toString().replace("<event>", "Spleef"));
			return;
		}

		activeSpleef.handleJoin(player);
	}

}
