package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "spleef host" }, permission = "practice.host")
public class SpleefHostCommand {

	public static void execute(Player player) {
		if (Practice.get().getSpleefManager().getActiveSpleef() != null) {
			player.sendMessage(CC.RED + "There is already an active Spleef Event.");
			return;
		}

		if (!Practice.get().getSpleefManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Spleef Event.");
			return;
		}

		Practice.get().getSpleefManager().setActiveSpleef(new Spleef(player));

		for (Player other : Practice.get().getServer().getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(other.getUniqueId());

			if (profile.isInLobby()) {
				if (!profile.getKitEditor().isActive()) {
					profile.refreshHotbar();
				}
			}
		}
	}

}
