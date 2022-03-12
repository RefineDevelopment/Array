package xyz.refinedev.practice.tournament.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.KitManager;
import xyz.refinedev.practice.tournament.menu.buttons.TournamentSelectKitButton;
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
 * Created: 9/28/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentSelectKitMenu extends Menu {

    private final FoldersConfigurationFile config;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return config.getString("TOURNAMENT_KIT_MENU.TITLE");
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
        for ( Kit kit : kitManager.getKits() ) {
            buttons.put(buttons.size(), new TournamentSelectKitButton(config, kit));
        }
        return buttons;
    }
}
