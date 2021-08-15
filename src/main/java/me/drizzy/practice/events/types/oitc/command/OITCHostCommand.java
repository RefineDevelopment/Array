package me.drizzy.practice.events.types.oitc.command;

import me.drizzy.practice.events.menu.EventSelectKitMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"OITC host"}, permission = "array.host.oitc")
public class OITCHostCommand {

    public static void execute(Player player) {
        new EventSelectKitMenu("OITC").openMenu(player);
    }

}
