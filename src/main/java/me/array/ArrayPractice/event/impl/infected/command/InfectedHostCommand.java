package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "infected host" }, permission = "practice.infected.host")
public class InfectedHostCommand {

	public static void execute(Player player) {
		if (Array.get().getInfectedManager().getActiveInfected() != null) {
			player.sendMessage(CC.RED + "There is already an active Infected Event.");
			return;
		}

		if (!Array.get().getInfectedManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Infected Event.");
			return;
		}

		Array.get().getInfectedManager().setActiveInfected(new Infected(player));

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
