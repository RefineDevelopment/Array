package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "gulag host" }, permission = "array.host.gulag")
public class GulagHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getGulagManager().getActiveGulag() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString());
			return;
		}

		if (!Array.getInstance().getGulagManager().getCooldown().hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString());
			return;
		}

		Array.getInstance().getGulagManager().setActiveGulag(new Gulag(player));

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
