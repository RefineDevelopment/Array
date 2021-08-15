package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef setspawn", permission = "array.dev")
public class SpleefSetSpawnCommand {

	public void execute(Player player) {
		Array.getInstance().getSpleefManager().setSpleefSpawn(player.getLocation());
		player.sendMessage(CC.translate("&7Updated &cSpleef's &7spawn location."));
		Array.getInstance().getSpleefManager().save();
	}

}
