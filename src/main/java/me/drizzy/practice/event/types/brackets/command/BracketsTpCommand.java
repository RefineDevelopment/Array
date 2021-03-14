package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets tp", permission = "array.staff")
public class BracketsTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());
		player.sendMessage(CC.GREEN + "Teleported to brackets's spawn location.");
	}

}
