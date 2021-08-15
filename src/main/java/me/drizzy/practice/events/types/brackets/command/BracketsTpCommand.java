package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets tp", permission = "array.dev")
public class BracketsTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());
		player.sendMessage(CC.translate("&8[&c&lArray&8] &7Teleported to &cBrackets's &7spawn location."));
	}

}
