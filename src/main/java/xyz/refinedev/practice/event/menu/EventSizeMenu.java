package xyz.refinedev.practice.event.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.buttons.EventSelectButton;
import xyz.refinedev.practice.event.menu.buttons.EventSizeButton;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/1/2021
 * Project: Array
 */

public class EventSizeMenu extends Menu {

    private final Array plugin = this.getPlugin();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("event_size");
    private final EventType eventType;

    public EventSizeMenu(EventType eventType) {
        this.loadMenu(config);

        this.eventType = eventType;
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return config.getInteger("SIZE");
    }


    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for ( EventTeamSize eventSize : EventTeamSize.values() ) {
            String path = "BUTTONS." + eventSize.name() + ".";
            buttons.put(config.getInteger(path + "SLOT"), new EventSizeButton(eventType, eventSize, config));
        }
        return buttons;
    }
}
