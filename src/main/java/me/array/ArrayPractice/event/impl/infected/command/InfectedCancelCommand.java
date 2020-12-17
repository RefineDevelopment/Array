package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "infected cancel", permission = "practice.infected.cancel")
public class InfectedCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getInfectedManager().getActiveInfected() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Infected event.");
			return;
		}

		Array.get().getInfectedManager().getActiveInfected().end("None");
		Bukkit.broadcastMessage(CC.DARK_AQUA + "(Infected)" + CC.RED + "Event" + CC.YELLOW + " was cancelled ");
	}

}
