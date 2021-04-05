package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.events.menu.EventSelectKitMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"lms host"}, permission = "array.host.lms")
public class LMSHostCommand {

    public static void execute(Player player) {
        new EventSelectKitMenu("LMS").openMenu(player);
    }

}
