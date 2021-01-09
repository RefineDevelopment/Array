package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.spleef.SpleefState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef join")
public class SpleefJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Spleef activeSpleef = Practice.getInstance().getSpleefManager().getActiveSpleef();

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

		Practice.getInstance().getSpleefManager().getActiveSpleef().handleJoin(player);
	}

}
