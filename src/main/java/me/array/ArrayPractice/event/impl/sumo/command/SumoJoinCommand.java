package me.array.ArrayPractice.event.impl.sumo.command;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.event.impl.sumo.SumoState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo join")
public class SumoJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Sumo activeSumo = Practice.getInstance().getSumoManager().getActiveSumo();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the Sumo Event right now.");
			return;
		}

		if (activeSumo == null) {
			player.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		if (activeSumo.getState() != SumoState.WAITING) {
			player.sendMessage(CC.RED + "That Sumo Event is currently on-going and cannot be joined.");
			return;
		}

		Practice.getInstance().getSumoManager().getActiveSumo().handleJoin(player);
	}

}
