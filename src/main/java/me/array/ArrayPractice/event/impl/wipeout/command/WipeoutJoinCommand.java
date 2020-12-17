package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout join")
public class WipeoutJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Wipeout activeWipeout = Array.get().getWipeoutManager().getActiveWipeout();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the wipeout right now.");
			return;
		}

		if (activeWipeout == null) {
			player.sendMessage(CC.RED + "There isn't any active Wipeout Events right now.");
			return;
		}

		if (activeWipeout.getState() != WipeoutState.WAITING) {
			player.sendMessage(CC.RED + "This Wipeout Event is currently on-going and cannot be joined.");
			return;
		}

		Array.get().getWipeoutManager().getActiveWipeout().handleJoin(player);
	}

}
