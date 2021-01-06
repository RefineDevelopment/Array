package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.menu.EventSelectKitMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"lms host"}, permission = "practice.lms.host")
public class LMSHostCommand {

    public static void execute(Player player) {
        new EventSelectKitMenu("LMS").openMenu(player);
    }

}
