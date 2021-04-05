package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cancel", permission = "array.staff")
public class BracketsCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getBracketsManager().getActiveBrackets() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Brackets events.");
			return;
		}

		Array.getInstance().getBracketsManager().getActiveBrackets().end();
	}

}
