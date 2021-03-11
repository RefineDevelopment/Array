package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "parkour host" }, permission = "practice.host.parkour")
public class ParkourHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getParkourManager().getActiveParkour() != null) {
			player.sendMessage(CC.RED + "There is already an active Parkour Event.");
			return;
		}

		if (!Array.getInstance().getParkourManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Parkour Event.");
			return;
		}

		Array.getInstance().getParkourManager().setActiveParkour(new Parkour(player));

		for (Player other : Array.getInstance().getServer().getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(other.getUniqueId());

			if (profile.isInLobby()) {
				if (!profile.getKitEditor().isActive()) {
					profile.refreshHotbar();
				}
			}
		}
	}

}
