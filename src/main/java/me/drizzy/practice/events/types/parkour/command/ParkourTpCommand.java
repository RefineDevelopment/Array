package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "parkour tp", permission = "array.staff")
public class ParkourTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
		player.sendMessage(CC.translate("&8[&c&lArray&8] &7Teleported to &cParkour's &7spawn location."));
	}

}
