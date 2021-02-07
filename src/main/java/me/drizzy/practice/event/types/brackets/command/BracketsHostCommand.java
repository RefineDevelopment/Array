package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.event.menu.EventSelectKitMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "brackets host" }, permission = "practice.host.brackets")
public class BracketsHostCommand {

	public static void execute(Player player) {
		new EventSelectKitMenu("Brackets").openMenu(player);
	}

}
