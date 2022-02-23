package xyz.refinedev.practice.profile.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
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
 * Created: 10/17/2021
 * Project: Array
 */

public class ProfileMenu extends Menu {

    //TODO: Add Statistics Button, Divisions Button, History Button, Global Stats being displayed normally in the menu
    //TODO: Display the clan, the player is in and if he's in a party then show it and if he's in a match show it and have an option to go and spectate it
    //TODO: Same goes for events and tournaments

    private final Player player;
    private final FoldersConfigurationFile config;

    public ProfileMenu(Array plugin, Player player) {
        super(plugin);

        this.player = player;
        this.config = plugin.getMenuHandler().getConfigByName("profile_menu");
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
    public int getSize() {
        return 54;
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

        return buttons;
    }
}
