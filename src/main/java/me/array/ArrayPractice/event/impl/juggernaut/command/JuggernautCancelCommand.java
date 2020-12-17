package me.array.ArrayPractice.event.impl.juggernaut.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "juggernaut cancel", permission = "practice.juggernaut.cancel")
public class JuggernautCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getJuggernautManager().getActiveJuggernaut() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Juggernaut event.");
			return;
		}

		Array.get().getJuggernautManager().getActiveJuggernaut().end("None");
	}

}
