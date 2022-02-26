package xyz.refinedev.practice.queue.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.QueueManager;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.queue.menu.button.QueueKitButton;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class QueueSelectKitMenu extends Menu {

    private final QueueType queueType;

    public QueueSelectKitMenu(Array plugin, QueueType queueType) {
        super(plugin);
        
        this.queueType = queueType;
    }


    @Override
    public String getTitle(Player player) {
        return "&7Select a Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        QueueManager queueManager = this.getPlugin().getQueueManager();
        for (Queue queue : queueManager.getQueues().values()) {
            if (queue.getType() != queueType) continue;
            buttons.put(buttons.size(), new QueueKitButton(this.getPlugin(), queue));
        }
        return buttons;
    }
}
