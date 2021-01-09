package me.array.ArrayPractice.event.impl.sumo.command;

import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo leave")
public class SumoLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Sumo activeSumo = Practice.getInstance().getSumoManager().getActiveSumo();

		if (activeSumo == null) {
			player.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Sumo Event.");
			return;
		}

		Practice.getInstance().getSumoManager().getActiveSumo().handleLeave(player);
	}

}
