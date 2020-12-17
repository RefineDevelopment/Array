package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout setspawn", permission = "practice.wipeout.setspawn")
public class WipeoutSetSpawnCommand {

	public void execute(Player player) {
		Array.get().getWipeoutManager().setWipeoutSpawn(player.getLocation());

		player.sendMessage(CC.GREEN + "Updated wipeout's spawn location.");

		Array.get().getWipeoutManager().save();
	}

}
