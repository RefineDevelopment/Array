package me.drizzy.practice.event;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.event.menu.EventSelectEventMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"events", "host", "host event"})
public class EventCommand {

    public void execute(Player player) {
        new EventSelectEventMenu().openMenu(player);
    }
}
