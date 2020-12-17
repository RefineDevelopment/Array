package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut leave")
public class JuggernautLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Juggernaut activeJuggernaut = Array.get().getJuggernautManager().getActiveJuggernaut();

		if (activeJuggernaut == null) {
			player.sendMessage(CC.RED + "There isn't any active Juggernaut Events.");
			return;
		}

		if (!profile.isInJuggernaut() || !activeJuggernaut.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Juggernaut Event.");
			return;
		}

		Array.get().getJuggernautManager().getActiveJuggernaut().handleLeave(player);
	}

}
