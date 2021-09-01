package xyz.refinedev.practice.events.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.events.menu.EventSizeMenu;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/1/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class EventSelectButton extends Button {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("event_host");
    private final String name;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        EventType eventType = EventType.getByName(name);
        return null;
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    public void clicked(Player player, ClickType clickType) {
        if (name.contains("Solo") || name.contains("Team")) {
            EventSizeMenu menu = new EventSizeMenu();
            menu.openMenu(player);

            Button.playSuccess(player);
        } else {
            EventType eventType = EventType.getByName(name);
            boolean event = plugin.getEventManager().hostByType(player, eventType);
            if (!event) {
                player.closeInventory();
                Button.playFail(player);
            }
        }
    }
}
