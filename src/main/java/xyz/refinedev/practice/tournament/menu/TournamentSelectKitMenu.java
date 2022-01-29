package xyz.refinedev.practice.tournament.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.tournament.menu.buttons.TournamentKitButton;
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

public class TournamentSelectKitMenu extends Menu {

    private final FoldersConfigurationFile config = this.getPlugin().getMenuHandler().getConfigByName("general");

    public TournamentSelectKitMenu(Array plugin) {
        super(plugin);
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("TOURNAMENT_KIT_MENU.TITLE");
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
        for ( Kit kit : this.getPlugin().getKitManager().getKits() ) {
            buttons.put(buttons.size(), new TournamentKitButton(kit));
        }
        return buttons;
    }
}
