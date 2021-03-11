package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.parkour.ParkourState;
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

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the parkour right now.");
			return;
		}

		if (activeParkour == null) {
			player.sendMessage(CC.RED + "There isn't any active Parkour Events right now.");
			return;
		}

		if (activeParkour.getState() != ParkourState.WAITING) {
			player.sendMessage(CC.RED + "This Parkour Event is currently on-going and cannot be joined.");
			return;
		}

		Array.getInstance().getParkourManager().getActiveParkour().handleJoin(player);
	}

}
