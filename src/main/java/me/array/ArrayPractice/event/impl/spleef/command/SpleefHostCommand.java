package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "spleef host" }, permission = "practice.spleef.host")
public class SpleefHostCommand {

	public static void execute(Player player) {
		if (Array.get().getSpleefManager().getActiveSpleef() != null) {
			player.sendMessage(CC.RED + "There is already an active Spleef Event.");
			return;
		}

		if (!Array.get().getSpleefManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Spleef Event.");
			return;
		}

		Array.get().getSpleefManager().setActiveSpleef(new Spleef(player));

		for (Player other : Array.get().getServer().getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(other.getUniqueId());

			if (profile.isInLobby()) {
				if (!profile.getKitEditor().isActive()) {
					profile.refreshHotbar();
				}
			}
		}
	}

}
