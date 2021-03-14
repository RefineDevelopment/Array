package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.event.types.spleef.Spleef;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef leave")
public class SpleefLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Spleef activeSpleef = Array.getInstance().getSpleefManager().getActiveSpleef();

		if (activeSpleef == null) {
			player.sendMessage(CC.RED + "There isn't any active Spleef Events.");
			return;
		}

		if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Spleef Event.");
			return;
		}

		Array.getInstance().getSpleefManager().getActiveSpleef().handleLeave(player);
	}

}
