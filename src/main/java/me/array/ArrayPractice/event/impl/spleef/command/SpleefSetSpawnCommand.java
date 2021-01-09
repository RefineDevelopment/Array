package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef setspawn", permission = "practice.staff")
public class SpleefSetSpawnCommand {

	public void execute(Player player) {
		Practice.getInstance().getSpleefManager().setSpleefSpectator(player.getLocation());

		player.sendMessage(CC.GREEN + "Set spleef's spawn location.");

		Practice.getInstance().getSpleefManager().save();
	}

}
