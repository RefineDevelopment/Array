package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.brackets.BracketsState;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets join")
public class BracketsJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Brackets activeBrackets = Array.getInstance().getBracketsManager().getActiveBrackets();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the brackets right now.");
			return;
		}

		if (activeBrackets == null) {
			player.sendMessage(CC.RED + "There isn't any active Brackets Events right now.");
			return;
		}

		if (activeBrackets.getState() != BracketsState.WAITING) {
			player.sendMessage(CC.RED + "This Brackets Event is currently on-going and cannot be joined.");
			return;
		}

		Array.getInstance().getBracketsManager().getActiveBrackets().handleJoin(player);
	}

}
