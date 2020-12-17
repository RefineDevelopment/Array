package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "wipeout host" }, permission = "practice.wipeout.host")
public class WipeoutHostCommand {

	public static void execute(Player player) {
		if (Array.get().getWipeoutManager().getActiveWipeout() != null) {
			player.sendMessage(CC.RED + "There is already an active Wipeout Event.");
			return;
		}

		if (!Array.get().getWipeoutManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is an active cooldown for the Wipeout Event.");
			return;
		}

		Array.get().getWipeoutManager().setActiveWipeout(new Wipeout(player));

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
