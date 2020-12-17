package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout leave")
public class WipeoutLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Wipeout activeWipeout = Array.get().getWipeoutManager().getActiveWipeout();

		if (activeWipeout == null) {
			player.sendMessage(CC.RED + "There isn't any active Wipeout Events.");
			return;
		}

		if (!profile.isInWipeout() || !activeWipeout.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Wipeout Event.");
			return;
		}

		Array.get().getWipeoutManager().getActiveWipeout().handleLeave(player);
	}

}
