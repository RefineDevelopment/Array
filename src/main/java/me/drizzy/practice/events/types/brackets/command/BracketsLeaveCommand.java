package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets leave")
public class BracketsLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Brackets activeBrackets = Array.getInstance().getBracketsManager().getActiveBrackets();

		if (activeBrackets == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
			return;
		}

		if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Brackets"));
			return;
		}

		Array.getInstance().getBracketsManager().getActiveBrackets().handleLeave(player);
	}

}
