package xyz.refinedev.practice.profile.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.divisions.menu.ProfileDivisionsMenu;
import xyz.refinedev.practice.profile.menu.buttons.DivisionsButton;
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

@RequiredArgsConstructor
public class ProfileMenu extends Menu {

    private static final String KEY = "PROFILE_MENU.";

    //TODO: Add Statistics Button, Divisions Button, History Button, Global Stats being displayed normally in the menu
    //TODO: Display the clan, the player is in and if he's in a party then show it and if he's in a match show it and have an option to go and spectate it
    //TODO: Same goes for events and tournaments

    private final Player target;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return this.getConfig().getString(KEY + "TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    public int getSize() {
        return this.getConfig().getInteger(KEY + "SIZE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(target.getUniqueId());

        buttons.put(27, new DivisionsButton(profile));

        return buttons;
    }
}
