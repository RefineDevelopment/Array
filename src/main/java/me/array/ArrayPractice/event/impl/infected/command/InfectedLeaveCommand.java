package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected leave")
public class InfectedLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Infected activeInfected = Array.get().getInfectedManager().getActiveInfected();

		if (activeInfected == null) {
			player.sendMessage(CC.RED + "There isn't any active Infected Events.");
			return;
		}

		if (!profile.isInInfected() || !activeInfected.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Infected Event.");
			return;
		}

		Array.get().getInfectedManager().getActiveInfected().handleLeave(player);
	}

}
