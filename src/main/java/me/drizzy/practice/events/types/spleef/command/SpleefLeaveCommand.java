package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.events.types.spleef.Spleef;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef leave")
public class SpleefLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Spleef activeSpleef = Array.getInstance().getSpleefManager().getActiveSpleef();

		if (activeSpleef == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
			return;
		}

		if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Spleef"));
			return;
		}
		activeSpleef.handleLeave(player);
	}

}
