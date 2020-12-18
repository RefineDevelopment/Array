package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.menu.EventSelectKitMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = { "ffa host" }, permission = "practice.ffa.host")
public class FFAHostCommand {

	public static void execute(Player player) {
		new EventSelectKitMenu("FFA").openMenu(player);
	}

}
