package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "brackets cancel", permission = "practice.staff")
public class BracketsCancelCommand {

	public void execute(CommandSender sender) {
		if (Practice.get().getBracketsManager().getActiveBrackets() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Brackets event.");
			return;
		}

		Practice.get().getBracketsManager().getActiveBrackets().end();
	}

}
