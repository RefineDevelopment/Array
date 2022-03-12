package xyz.refinedev.practice.queue.menu;

import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class QueueSelectKitMenu extends Menu {

    private final QueueType queueType;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return "&7Select a Kit";
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        QueueManager queueManager = plugin.getQueueManager();

        for (Queue queue : queueManager.getQueues().values()) {
            if (queue.getType() != queueType) continue;
            buttons.put(buttons.size(), new QueueKitButton(queue));
        }
        return buttons;
    }
}
