package xyz.refinedev.practice.profile.history.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/9/2021
 * Project: Array
 */

public class ProfileHistoryMenu extends Menu {

    private final Array plugin;
    private final FoldersConfigurationFile config;

    public ProfileHistoryMenu(Array plugin) {
        super(plugin);

        this.plugin = plugin;
        this.config = plugin.getMenuHandler().getConfigByName("profile_history");
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
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
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        return null;
    }
}
