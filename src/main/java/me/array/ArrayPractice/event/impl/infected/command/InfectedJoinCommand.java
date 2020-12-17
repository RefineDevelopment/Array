package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.event.impl.infected.InfectedState;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected join")
public class InfectedJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Infected activeInfected = Array.get().getInfectedManager().getActiveInfected();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the infected right now.");
			return;
		}

		if (activeInfected == null) {
			player.sendMessage(CC.RED + "There isn't any active Infected Events right now.");
			return;
		}

		if (activeInfected.getState() != InfectedState.WAITING) {
			player.sendMessage(CC.RED + "This Infected Event is currently on-going and cannot be joined.");
			return;
		}

		Array.get().getInfectedManager().getActiveInfected().handleJoin(player);
	}

}
