package me.drizzy.practice.events;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.events.menu.EventSelectEventMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"events", "host", "host events"})
public class EventCommand {

    public void execute(Player player) {
        new EventSelectEventMenu().openMenu(player);
    }
}
