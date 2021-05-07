package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour host"}, permission = "array.host.parkour")
public class ParkourHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getParkourManager().getActiveParkour() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString().replace("<event>", "Parkour"));
			return;
		}

		if (!Array.getInstance().getParkourManager().getCooldown().hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "Parkour"));
			return;
		}
		Array.getInstance().getParkourManager().setActiveParkour(new Parkour(player));
		Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
	}

}
