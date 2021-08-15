package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "spleef host" }, permission = "array.host.spleef")
public class SpleefHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getSpleefManager().getActiveSpleef() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString().replace("<event>", "Spleef"));
			return;
		}

		if (!Array.getInstance().getSpleefManager().getCooldown().hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "Spleef"));
			return;
		}

		Array.getInstance().getSpleefManager().setActiveSpleef(new Spleef(player));
		Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
	}

}
