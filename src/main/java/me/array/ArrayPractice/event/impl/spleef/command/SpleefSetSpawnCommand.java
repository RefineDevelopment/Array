package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef setspawn", permission = "practice.spleef.setspawn")
public class SpleefSetSpawnCommand {

	public void execute(Player player) {
		Array.get().getSpleefManager().setSpleefSpectator(player.getLocation());

		player.sendMessage(CC.GREEN + "Set spleef's spawn location.");

		Array.get().getSpleefManager().save();
	}

}
