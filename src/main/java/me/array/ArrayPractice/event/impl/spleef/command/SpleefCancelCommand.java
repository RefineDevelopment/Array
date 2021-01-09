package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cancel", permission = "practice.staff")
public class SpleefCancelCommand {

	public void execute(CommandSender sender) {
		if (Practice.getInstance().getSpleefManager().getActiveSpleef() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Spleef event.");
			return;
		}

		Practice.getInstance().getSpleefManager().getActiveSpleef().end();
	}

}
