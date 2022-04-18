package xyz.refinedev.practice.duel.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.duel.menu.buttons.DuelArenaButton;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DuelSelectArenaMenu extends PaginatedMenu {

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    @Override
    public String getPrePaginatedTitle(Array plugin, Player player) {
        return "&7Select an arena";
    }

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    @Override
    public Map<Integer, Button> getAllPagesButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        List<Arena> arenas = plugin.getArenaManager().getArenas().stream().filter(arena -> {
            if (!arena.isSetup()) return false;
            if (arena.isDuplicate()) return false;
            if (!arena.getKits().contains(profile.getDuelProcedure().getKit())) return false;
            return !profile.getDuelProcedure().getKit().getGameRules().isBuild() || arena.getType() != ArenaType.SHARED;

        }).collect(Collectors.toList());

        for ( Arena arena : arenas) {
            buttons.put(buttons.size(), new DuelArenaButton(this, arena));
        }

        return buttons;
    }

    /**
     * This method runs when the menu is closed
     *
     * @param player {@link Player} player viewing the menu
     */
    @Override
    public void onClose(Array plugin, Player player) {
        if (this.isClosedByMenu()) return;

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        profile.setDuelProcedure(null);
    }

}
