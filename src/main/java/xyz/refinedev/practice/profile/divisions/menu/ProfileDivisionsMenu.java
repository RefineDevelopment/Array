package xyz.refinedev.practice.profile.divisions.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.DivisionsManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.divisions.ProfileDivision;
import xyz.refinedev.practice.profile.divisions.menu.buttons.ProfileELODivisionsButton;
import xyz.refinedev.practice.profile.divisions.menu.buttons.ProfileXPDivisionsButton;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

//TODO: Include a back button in each and every profile menu button
@RequiredArgsConstructor
public class ProfileDivisionsMenu extends PaginatedMenu {

    private static final String KEY = "PROFILE_DIVISIONS.";
    private final Profile target;

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    @Override
    public String getPrePaginatedTitle(Array plugin, Player player) {
        return this.getConfig().getString(KEY + "TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getMaxItemsPerPage(Player player) {
        return this.getConfig().getInteger(KEY + "SIZE");
    }

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    @Override
    public Map<Integer, Button> getAllPagesButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        DivisionsManager divisionManager = plugin.getDivisionsManager();

        if (divisionManager.isXPBased()) {
            ProfileDivision division = divisionManager.getDivisionByXP(target.getExperience());
            buttons.put(buttons.size(), new ProfileXPDivisionsButton(target, division, this.getConfig()));
        } else {
            ProfileDivision division = divisionManager.getDivisionByELO(target.getGlobalElo());
            buttons.put(buttons.size(), new ProfileELODivisionsButton(target, division, this.getConfig()));
        }
        return buttons;
    }
}
