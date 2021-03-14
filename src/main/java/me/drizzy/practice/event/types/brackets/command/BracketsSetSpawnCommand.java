package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets setspawn", permission = "array.staff")
public class BracketsSetSpawnCommand {

	public void execute(Player player, @CPL("[one|two|spec]") String position) {
		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.RED + "The position must be one/two/spec.");
		} else {
			if (position.equals("one")) {
				Array.getInstance().getBracketsManager().setBracketsSpawn1(player.getLocation());
			} else if (position.equals("two")){
				Array.getInstance().getBracketsManager().setBracketsSpawn2(player.getLocation());
			} else {
				Array.getInstance().getBracketsManager().setBracketsSpectator(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated brackets's spawn location " + position + ".");

			Array.getInstance().getBracketsManager().save();
		}
	}

}
