package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut tp", permission = "practice.parkour.tp")
public class JuggernautTpCommand {

	public void execute(Player player) {
		player.teleport(Array.get().getJuggernautManager().getJuggernautSpectator());
		player.sendMessage(CC.GREEN + "Teleported to juggernaut's spawn location.");
	}

}
