package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.gulag.GulagState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "gulag join")
public class GulagJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Gulag activeGulag = Array.getInstance().getGulagManager().getActiveGulag();

		if (profile.isBusy() || profile.getParty() != null) {
			player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
			return;
		}

		if (activeGulag == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
			return;
		}

		if (activeGulag.getState() != GulagState.WAITING) {
			player.sendMessage(Locale.EVENT_ALREADY_STARED.toString().replace("<event>", "Gulag"));
			return;
		}

		activeGulag.handleJoin(player);
	}

}
