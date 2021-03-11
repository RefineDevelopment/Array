package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.spleef.SpleefState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef join")
public class SpleefJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Spleef activeSpleef = Array.getInstance().getSpleefManager().getActiveSpleef();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the spleef right now.");
			return;
		}

		if (activeSpleef == null) {
			player.sendMessage(CC.RED + "There isn't any active Spleef Events right now.");
			return;
		}

		if (activeSpleef.getState() != SpleefState.WAITING) {
			player.sendMessage(CC.RED + "This Spleef Event is currently on-going and cannot be joined.");
			return;
		}

		Array.getInstance().getSpleefManager().getActiveSpleef().handleJoin(player);
	}

}
