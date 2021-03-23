package me.drizzy.practice.event.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "gulag tp", permission = "array.dev")
public class GulagTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getGulagManager().getGulagSpectator());
		player.sendMessage(CC.GREEN + "Teleported to gulag's spawn location.");
	}

}
