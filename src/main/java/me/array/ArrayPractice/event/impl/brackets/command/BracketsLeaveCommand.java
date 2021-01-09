package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets leave")
public class BracketsLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Brackets activeBrackets = Practice.getInstance().getBracketsManager().getActiveBrackets();

		if (activeBrackets == null) {
			player.sendMessage(CC.RED + "There isn't any active Brackets Events.");
			return;
		}

		if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Brackets Event.");
			return;
		}

		Practice.getInstance().getBracketsManager().getActiveBrackets().handleLeave(player);
	}

}
