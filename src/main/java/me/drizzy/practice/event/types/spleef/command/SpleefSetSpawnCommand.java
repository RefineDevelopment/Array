package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef setspawn", permission = "array.staff")
public class SpleefSetSpawnCommand {

	public void execute(Player player) {
		Array.getInstance().getSpleefManager().setSpleefSpectator(player.getLocation());

		player.sendMessage(CC.GREEN + "Set spleef's spawn location.");

		Array.getInstance().getSpleefManager().save();
	}

}
