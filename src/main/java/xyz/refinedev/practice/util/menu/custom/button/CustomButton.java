package xyz.refinedev.practice.util.menu.custom.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.custom.ButtonData;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/14/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class CustomButton extends Button {

    private final ButtonData buttonData;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        return buttonData.getItem();
    }

    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        buttonData.handleClick(plugin, player, clickType);
    }
}
