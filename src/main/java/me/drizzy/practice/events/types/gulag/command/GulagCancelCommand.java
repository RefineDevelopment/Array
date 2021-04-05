package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "gulag cancel", permission = "array.staff")
public class GulagCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getGulagManager().getActiveGulag() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Gulag events.");
			return;
		}

		Array.getInstance().getGulagManager().getActiveGulag().end();
	}

}
