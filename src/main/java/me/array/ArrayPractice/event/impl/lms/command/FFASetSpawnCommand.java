package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "ffa setspawn", permission = "practice.ffa.setspawn")
public class FFASetSpawnCommand {

	public void execute(Player player) {
		Array.get().getFfaManager().setFfaSpectator(player.getLocation());

		player.sendMessage(CC.GREEN + "Updated ffa's spawn location.");

		Array.get().getFfaManager().save();
	}

}
