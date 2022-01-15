package xyz.refinedev.practice.event.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.buttons.EventSelectButton;
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
 * Created at 7/10/2021
 * Project: Array
 */

public class EventSelectMenu extends Menu {

    private final Array plugin = this.getPlugin();
    private final FoldersConfigurationFile config = plugin.getMenuHandler().getConfigByName("event_host");

    public EventSelectMenu() {
        this.loadMenu(plugin, config);
        this.setAutoUpdate(true);
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
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for ( EventType eventType : EventType.values() ) {
            String path = "BUTTONS." + eventType.name() + ".";
            buttons.put(config.getInteger(path + "SLOT"), new EventSelectButton(config, eventType));
        }
        return buttons;
    }
}
