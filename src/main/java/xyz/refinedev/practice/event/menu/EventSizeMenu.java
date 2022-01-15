package xyz.refinedev.practice.event.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.buttons.EventSizeButton;
import xyz.refinedev.practice.kit.Kit;
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
    private final FoldersConfigurationFile config = plugin.getMenuHandler().getConfigByName("event_size");
    private final EventType eventType;
    private final transient Kit kit;

    public EventSizeMenu(EventType eventType) {
        this.loadMenu(plugin, config);

        this.eventType = eventType;
        this.kit = null;
    }

    public EventSizeMenu(EventType eventType, Kit kit) {
        this.loadMenu(plugin, config);

        this.eventType = eventType;
        this.kit = kit;
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
            buttons.put(config.getInteger(path + "SLOT"), new EventSizeButton(eventType, eventSize, config, kit));
        }
        return buttons;
    }
}
