package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour setspawn", permission = "practice.staff")
public class ParkourSetSpawnCommand {

	public void execute(Player player) {
		Practice.get().getParkourManager().setParkourSpawn(player.getLocation());

		player.sendMessage(CC.GREEN + "Updated parkour's spawn location.");

		Practice.get().getParkourManager().save();
	}

}
