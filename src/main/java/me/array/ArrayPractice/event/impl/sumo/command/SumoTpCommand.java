package me.array.ArrayPractice.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo tp", permission = "practice.staff")
public class SumoTpCommand {

	public void execute(Player player) {
		player.teleport(Practice.get().getSumoManager().getSumoSpectator());
		player.sendMessage(CC.GREEN + "Teleported to sumo's spawn location.");
	}

}
