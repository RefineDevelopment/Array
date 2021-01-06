package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef tp", permission = "practice.staff")
public class SpleefTpCommand {

	public void execute(Player player) {
		player.teleport(Practice.get().getSpleefManager().getSpleefSpectator());
		player.sendMessage(CC.GREEN + "Teleported to spleef's spawn location.");
	}

}
