package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.brackets.BracketsState;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets join")
public class BracketsJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Brackets activeBrackets = Array.getInstance().getBracketsManager().getActiveBrackets();

		if (profile.isBusy() || profile.getParty() != null) {
			player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
			return;
		}

		if (activeBrackets == null) {
			player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
			return;
		}

		if (activeBrackets.getState() != BracketsState.WAITING) {
			player.sendMessage(Locale.EVENT_ALREADY_STARED.toString().replace("<event>", "Brackets"));
			return;
		}

		Array.getInstance().getBracketsManager().getActiveBrackets().handleJoin(player);
	}

}
