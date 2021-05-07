package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.parkour.ParkourState;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour join")
public class ParkourJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Parkour activeParkour = Array.getInstance().getParkourManager().getActiveParkour();

		if (profile.isBusy() || profile.getParty() != null) {
			player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
			return;
		}

		if (activeParkour == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
			return;
		}

		if (activeParkour.getState() != ParkourState.WAITING) {
			player.sendMessage(Locale.EVENT_ALREADY_STARED.toString().replace("<event>", "Parkour"));
			return;
		}
		activeParkour.handleJoin(player);
	}

}
