package me.array.ArrayPractice.event.impl.sumo.command;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "sumo host" }, permission = "practice.host")
public class SumoHostCommand {

	public static void execute(Player player) {
		if (Practice.get().getSumoManager().getActiveSumo() != null) {
			player.sendMessage(CC.RED + "There is already an active Sumo Event.");
			return;
		}

		if (!Practice.get().getSumoManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is a Sumo Event cooldown active.");
			return;
		}

		Practice.get().getSumoManager().setActiveSumo(new Sumo(player));

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
