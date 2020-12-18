package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.event.impl.lms.FFAState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "ffa join")
public class FFAJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		FFA activeFFA = Array.get().getFfaManager().getActiveFFA();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the ffa right now.");
			return;
		}

		if (activeFFA == null) {
			player.sendMessage(CC.RED + "There isn't any active FFA Events right now.");
			return;
		}

		if (activeFFA.getState() != FFAState.WAITING) {
			player.sendMessage(CC.RED + "This FFA Event is currently on-going and cannot be joined.");
			return;
		}

		Array.get().getFfaManager().getActiveFFA().handleJoin(player);
	}

}
