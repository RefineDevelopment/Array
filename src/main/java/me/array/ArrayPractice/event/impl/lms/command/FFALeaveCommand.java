package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "ffa leave")
public class FFALeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		FFA activeFFA = Array.get().getFfaManager().getActiveFFA();

		if (activeFFA == null) {
			player.sendMessage(CC.RED + "There isn't any active FFA Events.");
			return;
		}

		if (!profile.isInFfa() || !activeFFA.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active FFA Event.");
			return;
		}

		Array.get().getFfaManager().getActiveFFA().handleLeave(player);
	}

}
