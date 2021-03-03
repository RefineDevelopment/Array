package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets leave")
public class BracketsLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Brackets activeBrackets = Array.getInstance().getBracketsManager().getActiveBrackets();

		if (activeBrackets == null) {
			player.sendMessage(CC.RED + "There isn't any active Gulag Events.");
			return;
		}

		if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Gulag Event.");
			return;
		}

		Array.getInstance().getBracketsManager().getActiveBrackets().handleLeave(player);
	}

}
