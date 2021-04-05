package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.gulag.GulagManager;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "gulag setspawn", permission = "array.dev")
public class GulagSetSpawnCommand {

	public void execute(Player player, @CPL("[one|two|spec]") String position) {
		GulagManager gulag = Array.getInstance().getGulagManager();
		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.RED + "The position must be one/two/spec.");
		} else {
			if (position.equals("one")) {
				gulag.setGulagSpawn1(player.getLocation());
			} else if (position.equals("two")){
				gulag.setGulagSpawn2(player.getLocation());
			} else {
				gulag.setGulagSpectator(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated gulag's spawn location " + position + ".");

			gulag.save();
		}
	}

}
