package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour setspawn", permission = "array.staff")
public class ParkourSetSpawnCommand {

	public void execute(Player player) {
		Array.getInstance().getParkourManager().setParkourSpawn(player.getLocation());

		player.sendMessage(CC.GREEN + "Updated parkour's spawn location.");

		Array.getInstance().getParkourManager().save();
	}

}
