package me.drizzy.practice.event.types.gulag.command;

import me.drizzy.practice.event.menu.EventSelectKitMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "gulag host" }, permission = "practice.host.gulag")
public class GulagHostCommand {

	public static void execute(Player player) {
		new EventSelectKitMenu("Gulag").openMenu(player);
	}

}
