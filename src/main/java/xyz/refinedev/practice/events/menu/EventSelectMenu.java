package xyz.refinedev.practice.events.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.custom.ButtonData;
import xyz.refinedev.practice.util.menu.custom.button.CustomButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final List<ButtonData> customButtons = new ArrayList<>();

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("event_host");

    public EventSelectMenu() {
        List<ButtonData> custom = plugin.getMenuManager().loadCustomButtons(config);
        if (custom != null && !custom.isEmpty()) {
            this.customButtons.addAll(custom);
        }
        this.setAutoUpdate(true);
        this.setPlaceholder(config.getBoolean("PLACEHOLDER"));

        if (this.isPlaceholder()) {
            Material material;

            try {
                material = Material.valueOf(config.getString("PLACEHOLDER_BUTTON.MATERIAL"));
            } catch (Exception e) {
                plugin.logger("Invalid Placeholder Button on Menu Event Host Menu, turning off placeholder mode.");
                this.setPlaceholder(false);
                return;
            }

            ItemBuilder itemBuilder = new ItemBuilder(material);
            itemBuilder.name(config.getString("PLACEHOLDER_BUTTON.NAME"));
            if (config.getInteger("PLACEHOLDER_BUTTON.DATA") != 0) itemBuilder.durability(config.getInteger("PLACEHOLDER_BUTTON.DATA"));
            itemBuilder.lore(config.getStringList("PLACEHOLDER_BUTTON.LORE"));
            itemBuilder.clearFlags();

            this.setPlaceholderButton(Button.placeholder(itemBuilder.build()));
        }
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

        for ( ButtonData customButton : customButtons ) {
            buttons.put(customButton.getSlot(), new CustomButton(customButton));
        }
        return buttons;
    }
}
