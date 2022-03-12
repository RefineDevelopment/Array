package xyz.refinedev.practice.profile.settings.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.config.ConfigHandler;
import xyz.refinedev.practice.profile.settings.ProfileSettingsType;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 3/12/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ProfileSettingsMenu extends Menu {
    
    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return this.getConfig().getString("PROFILE-SETTINGS.TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return this.getConfig().getInteger("PROFILE-SETTINGS.SIZE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ConfigHandler configHandler = plugin.getConfigHandler();
        String key = "PROFILE-SETTINGS.BUTTONS.";

        for ( ProfileSettingsType type : ProfileSettingsType.values() ) {
            if (type.equals(ProfileSettingsType.TOGGLETABSTYLE) && !configHandler.isTAB_ENABLED()) continue;
            if (type.equals(ProfileSettingsType.TOGGLECPSONSCOREBOARD) && !configHandler.isCPS_SCOREBOARD_SETTING()) continue;
            if (type.equals(ProfileSettingsType.TOGGLEPINGONSCOREBOARD) && !configHandler.isPING_SCOREBOARD_SETTING()) continue;

            buttons.put(this.getConfig().getInteger(key + type.name() + ".SLOT"), new SettingsButton(this.getConfig(), type));
        }
        return buttons;
    }
}
