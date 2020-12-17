package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "wipeout tp", permission = "practice.wipeout.tp")
public class WipeoutTpCommand {

	public void execute(Player player) {
		player.teleport(Array.get().getWipeoutManager().getWipeoutSpawn());
		player.sendMessage(CC.GREEN + "Teleported to wipeout's spawn location.");
	}

}
