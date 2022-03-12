package xyz.refinedev.practice.util.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.custom.ButtonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public abstract class Menu {

    private final List<ButtonData> customButtons = new ArrayList<>();
    private Map<Integer, Button> buttons = new HashMap<>();

    private BasicConfigurationFile config;

    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private boolean placeholder = false;

    private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);

    public void loadConfig(BasicConfigurationFile config) {
        this.config = config;
    }

    /**
     * Returns the size of buttons
     *
     * @param buttons {@link HashMap}
     * @return The amount of buttons
     */
    public int size(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    public int getSize() {
        return -1;
    }

    /**
     * Get slot at a particular x and y
     *
     * @return The slot
     */
    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    public abstract String getTitle(Array plugin, Player player);

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    public abstract Map<Integer, Button> getButtons(Array plugin, Player player);

    /**
     * This method runs when the menu is opened
     *
     * @param player {@link Player} player viewing the menu
     */
    public void onOpen(Array plugin, Player player) {
    }

    /**
     * This method runs when the menu is closed
     *
     * @param player {@link Player} player viewing the menu
     */
    public void onClose(Array plugin, Player player) {
    }

}
