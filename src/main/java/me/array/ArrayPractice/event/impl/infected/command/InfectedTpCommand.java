package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected tp", permission = "practice.parkour.tp")
public class InfectedTpCommand {

	public void execute(Player player) {
		player.teleport(Array.get().getInfectedManager().getInfectedSpawn1());
		player.sendMessage(CC.GREEN + "Teleported to infected's spawn location.");
	}

}
