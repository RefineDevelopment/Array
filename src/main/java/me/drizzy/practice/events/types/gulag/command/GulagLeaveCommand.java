package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "gulag leave")
public class GulagLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Gulag activeGulag = Array.getInstance().getGulagManager().getActiveGulag();

		if (activeGulag == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
			return;
		}

		if (!profile.isInGulag() || !activeGulag.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Gulag"));
			return;
		}

		activeGulag.handleLeave(player);
	}

}
