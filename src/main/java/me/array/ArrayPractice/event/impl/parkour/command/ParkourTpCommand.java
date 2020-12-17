package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour tp", permission = "practice.parkour.tp")
public class ParkourTpCommand {

	public void execute(Player player) {
		player.teleport(Array.get().getParkourManager().getParkourSpawn());
		player.sendMessage(CC.GREEN + "Teleported to parkour's spawn location.");
	}

}
