package me.array.ArrayPractice.event.impl.infected.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "infected setspawn", permission = "practice.infected.setspawn")
public class InfectedSetSpawnCommand {

	public void execute(Player player, @CPL("one/two") String position) {
		if (!(position.equals("one") || position.equals("two"))) {
			player.sendMessage(CC.RED + "The position must be one/two.");
		} else {
			if (position.equals("one")) {
				Array.get().getInfectedManager().setInfectedSpawn1(player.getLocation());
			} else if (position.equals("two")){
				Array.get().getInfectedManager().setInfectedSpawn2(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated infected's spawn location " + position + ".");

			Array.get().getInfectedManager().save();
		}
	}

}
