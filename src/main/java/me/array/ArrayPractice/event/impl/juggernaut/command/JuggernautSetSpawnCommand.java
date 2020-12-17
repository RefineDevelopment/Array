package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "juggernaut setspawn", permission = "practice.juggernaut.setspawn")
public class JuggernautSetSpawnCommand {

	public void execute(Player player) {
		Array.get().getJuggernautManager().setJuggernautSpectator(player.getLocation());

		player.sendMessage(CC.GREEN + "Updated juggernaut's spawn location.");

		Array.get().getJuggernautManager().save();
	}

}
