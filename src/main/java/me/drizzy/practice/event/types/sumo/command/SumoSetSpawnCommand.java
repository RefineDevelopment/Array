package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo setspawn", permission = "array.staff")
public class SumoSetSpawnCommand {

	public void execute(Player player, @CPL("[one|two|spec]") String position) {
		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.RED + "The position must be 1 or 2.");
		} else {
			if (position.equals("one")) {
				Array.getInstance().getSumoManager().setSumoSpawn1(player.getLocation());
			} else if (position.equals("two")) {
				Array.getInstance().getSumoManager().setSumoSpawn2(player.getLocation());
			} else {
				Array.getInstance().getSumoManager().setSumoSpectator(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated sumo's spawn location " + position + ".");

			Array.getInstance().getSumoManager().save();
		}
	}

}
