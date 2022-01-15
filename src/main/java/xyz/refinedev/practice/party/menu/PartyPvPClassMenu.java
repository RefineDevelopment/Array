package xyz.refinedev.practice.party.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.buttons.PartyHCFButton;
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

    private final Array plugin;

    public PartyPvPClassMenu(Array plugin) {
        this.plugin = plugin;

        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Select Armor Class";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Party party = plugin.getPartyManager().getPartyByUUID(player.getUniqueId());
        party.getKits().keySet().removeIf(party::containsPlayer);

        if (!party.getPlayers().isEmpty()) {
            party.getPlayers().forEach(target -> buttons.put(buttons.size(), new PartyHCFButton(plugin, target.getUniqueId())));
        }
        return buttons;
    }

}    