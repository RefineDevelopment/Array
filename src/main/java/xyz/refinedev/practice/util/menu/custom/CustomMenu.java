package xyz.refinedev.practice.util.menu.custom;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.custom.button.CustomButton;

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

@RequiredArgsConstructor
public class CustomMenu extends Menu {

    private final MenuData menuData;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
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
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        for ( ButtonData buttonData : menuData.getButtons() ) {
            buttonMap.put(buttonData.getSlot(), new CustomButton(buttonData));
        }
        return buttonMap;
    }


}
