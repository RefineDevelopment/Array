package me.array.ArrayPractice.event.impl.sumo.command;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cancel", permission = "practice.sumo.cancel")
public class SumoCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getSumoManager().getActiveSumo() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		Array.get().getSumoManager().getActiveSumo().end();
	}

}
