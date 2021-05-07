package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour leave")
public class ParkourLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Parkour activeParkour = Array.getInstance().getParkourManager().getActiveParkour();

		if (activeParkour == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
			return;
		}

		if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Parkour"));
			return;
		}
		activeParkour.handleLeave(player);
	}

}
