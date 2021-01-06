package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets tp", permission = "practice.staff")
public class BracketsTpCommand {

	public void execute(Player player) {
		player.teleport(Practice.get().getBracketsManager().getBracketsSpectator());
		player.sendMessage(CC.GREEN + "Teleported to brackets's spawn location.");
	}

}
