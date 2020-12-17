package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.LocationUtil;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars setspawn", permission = "practice.skywars.setspawn")
public class SkyWarsSetSpawnCommand {

	public void execute(Player player) {
		Array.get().getSkyWarsManager().getSkyWarsSpectators().add(LocationUtil.serialize(player.getLocation()));

		player.sendMessage(CC.GREEN + "Added skywars's spawn location.");

		Array.get().getSkyWarsManager().save();
	}

}
