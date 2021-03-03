package me.drizzy.practice.event.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "gulag cancel", permission = "array.staff")
public class GulagCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getGulagManager().getActiveGulag() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Gulag event.");
			return;
		}

		Array.getInstance().getGulagManager().getActiveGulag().end();
	}

}
