package me.array.ArrayPractice.event.impl.ffa.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "ffa cancel", permission = "practice.ffa.cancel")
public class FFACancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getFfaManager().getActiveFFA() == null) {
			sender.sendMessage(CC.RED + "There isn't an active FFA event.");
			return;
		}

		Array.get().getFfaManager().getActiveFFA().end();
	}

}
