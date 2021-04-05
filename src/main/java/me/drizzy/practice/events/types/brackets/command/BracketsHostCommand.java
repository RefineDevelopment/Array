package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.events.menu.EventSelectKitMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "brackets host" }, permission = "array.host.brackets")
public class BracketsHostCommand {

	public static void execute(Player player) {
		new EventSelectKitMenu("Brackets").openMenu(player);
	}

}
