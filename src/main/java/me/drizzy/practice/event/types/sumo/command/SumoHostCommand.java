package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "sumo host" }, permission = "practice.sumo.host")
public class SumoHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getSumoManager().getActiveSumo() != null) {
			player.sendMessage(CC.RED + "There is already an active Sumo Event.");
			return;
		}

		if (!Array.getInstance().getSumoManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is a Sumo Event cooldown active.");
			return;
		}

		Array.getInstance().getSumoManager().setActiveSumo(new Sumo(player));

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
