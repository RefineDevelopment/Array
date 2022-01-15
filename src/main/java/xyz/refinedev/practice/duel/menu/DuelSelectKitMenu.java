package xyz.refinedev.practice.duel.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.duel.menu.buttons.DuelKitButton;
import xyz.refinedev.practice.kit.Kit;
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

@RequiredArgsConstructor
public class DuelSelectKitMenu extends Menu {

    private final Array plugin;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return "&7Select a kit";
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Map<Integer, Button> buttons = new HashMap<>();

        if (profile.hasParty() && plugin.getConfigHandler().isHCF_ENABLED()) {
            for ( Kit kit : plugin.getKitManager().getKits()) {
                buttons.put(buttons.size(), new DuelKitButton(plugin, this, kit));
            }
        } else {
            for ( Kit kit : plugin.getKitManager().getKits() ) {
                if (kit.equals(plugin.getKitManager().getTeamFight())) continue;

                buttons.put(buttons.size(), new DuelKitButton(plugin, this, kit));
            }
        }
        return buttons;
    }

    /**
     * This method runs when the menu is closed
     *
     * @param player {@link Player} player viewing the menu
     */
    @Override
    public void onClose(Player player) {
        if (this.isClosedByMenu()) return;

        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        profile.setDuelProcedure(null);
    }
}
