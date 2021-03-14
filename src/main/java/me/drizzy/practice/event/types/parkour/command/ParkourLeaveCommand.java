package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.event.types.parkour.Parkour;
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
			player.sendMessage(CC.RED + "There isn't any active Parkour Events.");
			return;
		}

		if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Parkour Event.");
			return;
		}

		Array.getInstance().getParkourManager().getActiveParkour().handleLeave(player);
	}

}
