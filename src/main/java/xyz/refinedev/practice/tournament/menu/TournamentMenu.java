package xyz.refinedev.practice.tournament.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.Map;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 3/12/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentMenu extends Menu {

    private Kit kit = this.getKit();
    private int teamSize = 1;
    private int maxPlayers = 10;

    /**
     * Get menu's title
     *
     * @param plugin
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return null;
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param plugin
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        return null;
    }
}
