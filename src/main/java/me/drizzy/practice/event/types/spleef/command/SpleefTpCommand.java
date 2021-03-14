package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef tp", permission = "array.staff")
public class SpleefTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getSpleefManager().getSpleefSpectator());
		player.sendMessage(CC.GREEN + "Teleported to spleef's spawn location.");
	}

}
