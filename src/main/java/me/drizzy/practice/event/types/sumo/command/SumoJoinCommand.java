package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.event.types.sumo.SumoState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo join")
public class SumoJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Sumo activeSumo = Array.getInstance().getSumoManager().getActiveSumo();

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

		Array.getInstance().getSumoManager().getActiveSumo().handleJoin(player);
	}

}
