package xyz.refinedev.practice.match.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.menu.buttons.SpectateButton;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.util.chat.CC;
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
 * Created: 8/14/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchSpectateMenu extends Menu {

    private final FoldersConfigurationFile config;
    private final Match match;

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return CC.translate(config.getString("SPECTATING_MENU.TITLE"));
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
        for ( TeamPlayer teamPlayer : match.getTeamPlayers()) {
            buttons.put(buttons.size(), new SpectateButton(match, teamPlayer));
        }
        return buttons;
    }
}
