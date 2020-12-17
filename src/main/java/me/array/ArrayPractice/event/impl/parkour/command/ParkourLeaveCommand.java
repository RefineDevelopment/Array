package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour leave")
public class ParkourLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Parkour activeParkour = Array.get().getParkourManager().getActiveParkour();

		if (activeParkour == null) {
			player.sendMessage(CC.RED + "There isn't any active Parkour Events.");
			return;
		}

		if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Parkour Event.");
			return;
		}

		Array.get().getParkourManager().getActiveParkour().handleLeave(player);
	}

}
