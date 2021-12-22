package xyz.refinedev.practice.event.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.buttons.EventKitButton;
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
 * Created at 7/10/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class EventKitMenu extends Menu {

    private final Array plugin = this.getPlugin();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("general");
    private final EventType type;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("EVENT_KIT_MENU.TITLE");
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
        for ( Kit kit : this.plugin.getKitManager().getKits() ) {
            buttons.put(buttons.size(), new EventKitButton(type, kit));
        }
        return buttons;
    }
}
