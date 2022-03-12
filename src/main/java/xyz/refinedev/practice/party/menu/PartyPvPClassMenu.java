package xyz.refinedev.practice.party.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.PartyManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.buttons.PartyHCFButton;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/27/2021
 * Project: Array
 */

public class PartyPvPClassMenu extends PaginatedMenu {

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    @Override
    public String getPrePaginatedTitle(Array plugin, Player player) {
        return "&7Select Armor Class";
    }

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    @Override
    public Map<Integer, Button> getAllPagesButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ProfileManager profileManager = plugin.getProfileManager();
        PartyManager partyManager = plugin.getPartyManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());
        party.getKits().keySet().removeIf(party::containsPlayer);

        if (!party.getPlayers().isEmpty()) {
            for ( Player target : party.getPlayers() ) {
                buttons.put(buttons.size(), new PartyHCFButton(target.getUniqueId()));
            }
        }
        return buttons;
    }

}    