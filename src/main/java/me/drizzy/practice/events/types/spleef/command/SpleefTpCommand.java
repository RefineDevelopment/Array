package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef tp", permission = "array.dev")
public class SpleefTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getSpleefManager().getSpleefSpawn());
		player.sendMessage(CC.translate("&8[&c&lArray&8] &7Teleported to &cSpleef's &7spawn location."));
	}

}
