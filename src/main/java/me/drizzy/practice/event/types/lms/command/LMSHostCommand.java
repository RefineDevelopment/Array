package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.event.menu.EventSelectKitMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"lms host"}, permission = "practice.host.lms")
public class LMSHostCommand {

    public static void execute(Player player) {
        new EventSelectKitMenu("LMS").openMenu(player);
    }

}
