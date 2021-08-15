package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.brackets.BracketsManager;
import me.drizzy.practice.events.types.sumo.SumoManager;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo setspawn", permission = "array.dev")
public class SumoSetSpawnCommand {

	public void execute(Player player, @CPL("[one|two|spec]") String position) {
		SumoManager sumo = Array.getInstance().getSumoManager();

		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.translate("&7The position must be &cone&7/&ctwo&7/&cspec&7."));
		} else {
			if (position.equals("one"))  {
				sumo.setSumoSpawn1(player.getLocation());
			} else if (position.equals("two")){
				sumo.setSumoSpawn2(player.getLocation());
			} else {
				sumo.setSumoSpectator(player.getLocation());
			}

			player.sendMessage(CC.translate("&7Updated &cSumo's &7spawn location &c" + position + "&7."));
			sumo.save();
		}
	}

}
