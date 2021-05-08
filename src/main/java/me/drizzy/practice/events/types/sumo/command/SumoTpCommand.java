package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo tp", permission = "array.dev")
public class SumoTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());
		player.sendMessage(CC.translate("&8[&c&lArray&8] &7Teleported to &cSumo's &7spawn location."));
	}

}
