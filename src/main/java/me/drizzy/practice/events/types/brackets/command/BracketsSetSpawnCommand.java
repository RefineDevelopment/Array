package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.events.types.brackets.BracketsManager;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "brackets setspawn", permission = "array.dev")
public class BracketsSetSpawnCommand {

	public void execute(Player player, @CPL("[one|two|spec]") String position) {
		BracketsManager brackets = Array.getInstance().getBracketsManager();

		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.translate("&7The position must be &cone&7/&ctwo&7/&cspec&7."));
		} else {
			if (position.equals("one"))  {
				brackets.setBracketsSpawn1(player.getLocation());
			} else if (position.equals("two")){
				brackets.setBracketsSpawn2(player.getLocation());
			} else {
				brackets.setBracketsSpectator(player.getLocation());
			}

			player.sendMessage(CC.translate("&7Updated &cBrackets's &7spawn location &c" + position + "&7."));
			brackets.save();
		}
	}

}
