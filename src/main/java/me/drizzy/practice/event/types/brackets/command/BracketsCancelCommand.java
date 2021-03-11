package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cancel", permission = "array.staff")
public class BracketsCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getBracketsManager().getActiveBrackets() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Brackets event.");
			return;
		}

		Array.getInstance().getBracketsManager().getActiveBrackets().end();
	}

}
