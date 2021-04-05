package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cancel", permission = "array.staff")
public class SpleefCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSpleefManager().getActiveSpleef() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Spleef events.");
			return;
		}

		Array.getInstance().getSpleefManager().getActiveSpleef().end();
	}

}
