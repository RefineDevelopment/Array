package xyz.refinedev.practice.party.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.buttons.PartyDuelButton;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;
import xyz.refinedev.practice.util.other.SkullCreator;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.util.menu.Button;
import org.bukkit.entity.Player;

public class PartyDuelMenu extends PaginatedMenu {

    {setPlaceholder(true);
     setAutoUpdate(true);}

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Other Parties";
    }
    
    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<Party> parties = new ArrayList<>(Party.getParties());
        int index = 0;

        parties.sort(Comparator.comparing(p -> p.getPlayers().size()));

        for (Party party : parties) {
            Profile profile = Profile.getByUuid(party.getLeader().getUuid());
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
