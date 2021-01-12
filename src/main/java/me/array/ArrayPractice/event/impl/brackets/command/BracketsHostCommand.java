package me.array.ArrayPractice.event.impl.brackets.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.menu.EventSelectKitMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = { "brackets host" }, permission = "practice.host")
public class BracketsHostCommand {

	public static void execute(Player player) {
		new EventSelectKitMenu("Brackets").openMenu(player);
	}

}
