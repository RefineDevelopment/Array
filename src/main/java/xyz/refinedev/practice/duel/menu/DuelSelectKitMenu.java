package xyz.refinedev.practice.duel.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.duel.menu.buttons.DuelKitButton;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.KitManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/14/2022
 * Project: Array
 */

public class DuelSelectKitMenu extends Menu {

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return "&7Select a kit";
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

        KitManager kitManager = plugin.getKitManager();
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        for ( Kit kit : kitManager.getKits() ) {
            if (profile.hasParty() && plugin.getConfigHandler().isHCF_ENABLED()) {
                if (kit.equals(kitManager.getTeamFight())) continue;
            }

            buttons.put(buttons.size(), new DuelKitButton(this, kit));
        }

        return buttons;
    }

    /**
     * This method runs when the menu is closed
     *
     * @param player {@link Player} player viewing the menu
     */
    @Override
    public void onClose(Array plugin, Player player) {
        if (this.isClosedByMenu()) return;

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        profile.setDuelProcedure(null);
    }
}
