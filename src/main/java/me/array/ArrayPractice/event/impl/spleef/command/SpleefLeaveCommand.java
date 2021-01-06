package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef leave")
public class SpleefLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Spleef activeSpleef = Practice.get().getSpleefManager().getActiveSpleef();

		if (activeSpleef == null) {
			player.sendMessage(CC.RED + "There isn't any active Spleef Events.");
			return;
		}

		if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Spleef Event.");
			return;
		}

		Practice.get().getSpleefManager().getActiveSpleef().handleLeave(player);
	}

}
