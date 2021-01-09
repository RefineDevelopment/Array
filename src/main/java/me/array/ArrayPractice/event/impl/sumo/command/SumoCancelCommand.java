package me.array.ArrayPractice.event.impl.sumo.command;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cancel", permission = "practice.staff")
public class SumoCancelCommand {

	public void execute(CommandSender sender) {
		if (Practice.getInstance().getSumoManager().getActiveSumo() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		Practice.getInstance().getSumoManager().getActiveSumo().end();
	}

}
