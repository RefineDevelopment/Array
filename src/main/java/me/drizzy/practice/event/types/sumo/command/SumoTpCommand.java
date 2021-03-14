package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo tp", permission = "practice.sumo.tp")
public class SumoTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());
		player.sendMessage(CC.GREEN + "Teleported to sumo's spawn location.");
	}

}
