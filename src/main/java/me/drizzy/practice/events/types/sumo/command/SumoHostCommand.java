package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo host"}, permission = "array.host.sumo")
public class SumoHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getSumoManager().getActiveSumo() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString().replace("<event>", "Sumo"));
			return;
		}

		if (!Array.getInstance().getSumoManager().getCooldown().hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "Sumo"));
			return;
		}
		Array.getInstance().getSumoManager().setActiveSumo(new Sumo(player));
		Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
	}

}
