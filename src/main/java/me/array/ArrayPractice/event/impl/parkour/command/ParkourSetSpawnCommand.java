package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour setspawn", permission = "practice.parkour.setspawn")
public class ParkourSetSpawnCommand {

	public void execute(Player player) {
		Array.get().getParkourManager().setParkourSpawn(player.getLocation());

		player.sendMessage(CC.GREEN + "Updated parkour's spawn location.");

		Array.get().getParkourManager().save();
	}

}
