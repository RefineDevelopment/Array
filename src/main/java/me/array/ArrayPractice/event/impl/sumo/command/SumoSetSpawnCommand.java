package me.array.ArrayPractice.event.impl.sumo.command;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo setspawn", permission = "practice.staff")
public class SumoSetSpawnCommand {

	public void execute(Player player, @CPL("one/two/spec") String position) {
		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.RED + "The position must be 1 or 2.");
		} else {
			if (position.equals("one")) {
				Array.get().getSumoManager().setSumoSpawn1(player.getLocation());
			} else if (position.equals("two")) {
				Array.get().getSumoManager().setSumoSpawn2(player.getLocation());
			} else {
				Array.get().getSumoManager().setSumoSpectator(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated sumo's spawn location " + position + ".");

			Array.get().getSumoManager().save();
		}
	}

}
