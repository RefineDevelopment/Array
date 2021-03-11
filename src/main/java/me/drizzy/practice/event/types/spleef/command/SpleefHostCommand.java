package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "spleef host" }, permission = "practice.host.spleef")
public class SpleefHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getSpleefManager().getActiveSpleef() != null) {
			player.sendMessage(CC.RED + "There is already an active Spleef Event.");
			return;
		}

		if (!Array.getInstance().getSpleefManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Spleef Event.");
			return;
		}

		Array.getInstance().getSpleefManager().setActiveSpleef(new Spleef(player));

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
