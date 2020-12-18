package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "ffa tp", permission = "practice.ffa.tp")
public class FFATpCommand {

	public void execute(Player player) {
		player.teleport(Array.get().getFfaManager().getFfaSpectator());
		player.sendMessage(CC.GREEN + "Teleported to ffa's spawn location.");
	}

}
