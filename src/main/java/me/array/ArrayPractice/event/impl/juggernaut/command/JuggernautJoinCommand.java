package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautState;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut join")
public class JuggernautJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Juggernaut activeJuggernaut = Array.get().getJuggernautManager().getActiveJuggernaut();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the juggernaut right now.");
			return;
		}

		if (activeJuggernaut == null) {
			player.sendMessage(CC.RED + "There isn't any active Juggernaut Events right now.");
			return;
		}

		if (activeJuggernaut.getState() != JuggernautState.WAITING) {
			player.sendMessage(CC.RED + "This Juggernaut Event is currently on-going and cannot be joined.");
			return;
		}

		Array.get().getJuggernautManager().getActiveJuggernaut().handleJoin(player);
	}

}
