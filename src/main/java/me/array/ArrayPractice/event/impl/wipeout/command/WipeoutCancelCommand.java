package me.array.ArrayPractice.event.impl.wipeout.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "wipeout cancel", permission = "practice.wipeout.cancel")
public class WipeoutCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getWipeoutManager().getActiveWipeout() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Wipeout event.");
			return;
		}

		Array.get().getWipeoutManager().getActiveWipeout().end(null);
	}

}
