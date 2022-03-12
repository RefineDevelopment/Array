package xyz.refinedev.practice.util.menu.custom;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.custom.button.CustomButton;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/11/2021
 * Project: Array
 */

public class CustomPaginatedMenu extends PaginatedMenu {

    private final MenuData menuData;

    public CustomPaginatedMenu(MenuData menuData) {

        this.menuData = menuData;

        this.setPlaceholder(menuData.isPlaceholder());

        if (this.isPlaceholder()) {
            ItemStack item = menuData.getPlaceholderItem();
            this.setPlaceholderButton(Button.placeholder(item));
        }
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getPrePaginatedTitle(Array plugin, Player player) {
        return menuData.getTitle();
    }

    /**
     * Returns the Menu's Inventory Size
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return menuData.getSize();
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getAllPagesButtons(Array plugin, Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        for ( ButtonData buttonData : menuData.getButtons() ) {
            buttonMap.put(buttonData.getSlot(), new CustomButton(buttonData));
        }
        return buttonMap;
    }
}
