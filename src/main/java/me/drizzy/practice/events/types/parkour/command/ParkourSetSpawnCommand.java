package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour setspawn", permission = "array.staff")
public class ParkourSetSpawnCommand {

	public void execute(Player player) {
		Array.getInstance().getParkourManager().setParkourSpawn(player.getLocation());
		player.sendMessage(CC.translate("&7Updated &cParkour's &7spawn location."));
		Array.getInstance().getParkourManager().save();
	}

}
