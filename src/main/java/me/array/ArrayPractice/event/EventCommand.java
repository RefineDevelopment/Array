package me.array.ArrayPractice.event;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.event.menu.EventSelectEventMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"event", "events", "host", "host event"})
public class EventCommand {

    public void execute(Player player) {
        new EventSelectEventMenu().openMenu(player);
    }
}
