package me.drizzy.practice.event.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.gulag.Gulag;
import me.drizzy.practice.event.types.gulag.GulagState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "gulag join")
public class GulagJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Gulag activeGulag= Array.getInstance().getGulagManager().getActiveGulag();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the gulag right now.");
			return;
		}

		if (activeGulag == null) {
			player.sendMessage(CC.RED + "There isn't any active Gulag Events right now.");
			return;
		}

		if (activeGulag.getState() != GulagState.WAITING) {
			player.sendMessage(CC.RED + "This Gulag Event is currently on-going and cannot be joined.");
			return;
		}

		activeGulag.handleJoin(player);
	}

}
