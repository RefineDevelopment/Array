package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "juggernaut host" }, permission = "practice.juggernaut.host")
public class JuggernautHostCommand {

	public static void execute(Player player) {
		if (Array.get().getJuggernautManager().getActiveJuggernaut() != null) {
			player.sendMessage(CC.RED + "There is already an active Juggernaut Event.");
			return;
		}

		if (!Array.get().getJuggernautManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Juggernaut Event.");
			return;
		}

		Array.get().getJuggernautManager().setActiveJuggernaut(new Juggernaut(player));

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
