package xyz.refinedev.practice.party.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.PartyManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.buttons.PartyDuelButton;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.*;

public class PartyDuelMenu extends PaginatedMenu {
    
    public PartyDuelMenu() {
        this.setPlaceholder(true);
        this.setAutoUpdate(true);
    }

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    @Override
    public String getPrePaginatedTitle(Array plugin, Player player) {
        return "&7Other Parties";
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

        List<Party> parties = new ArrayList<>(partyManager.getParties().values());
        int index = 0;

        parties.sort(Comparator.comparing(p -> p.getPlayers().size()));

        for (Party party : parties) {
            Profile profile = profileManager.getProfile(party.getLeader().getUniqueId());
            if (party.isMember(player.getUniqueId()) || !profile.getSettings().isReceiveDuelRequests()) continue;

            buttons.put(index++, new PartyDuelButton(party));
        }
        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 5; // top row is dedicated to switching
    }
}
