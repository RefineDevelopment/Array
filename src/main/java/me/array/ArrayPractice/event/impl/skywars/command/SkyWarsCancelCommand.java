package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "skywars cancel", permission = "practice.skywars.cancel")
public class SkyWarsCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.get().getSkyWarsManager().getActiveSkyWars() == null) {
			sender.sendMessage(CC.RED + "There isn't an active SkyWars event.");
			return;
		}

		Array.get().getSkyWarsManager().getActiveSkyWars().end();
	}

}
