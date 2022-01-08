package xyz.refinedev.practice.duel.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DuelSelectArenaMenu extends PaginatedMenu {

    private final Array plugin = this.getPlugin();

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Select an arena";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

        List<Arena> arenas = plugin.getArenaManager().getArenas().stream().filter(arena -> {
            if (!arena.isSetup()) return false;
            if (arena.isDuplicate()) return false;
            if (!arena.getKits().contains(profile.getDuelProcedure().getKit())) return false;
            return !profile.getDuelProcedure().getKit().getGameRules().isBuild() || arena.getType() != ArenaType.SHARED;

        }).collect(Collectors.toList());

        for ( Arena arena : arenas) {
            buttons.put(buttons.size(), new SelectArenaButton(arena));
        }

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (this.isClosedByMenu()) return;

        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        profile.setDuelProcedure(null);
    }

    @AllArgsConstructor
    private class SelectArenaButton extends Button {

        private final Arena arena;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(arena.getDisplayIcon())
                    .name(arena.getDisplayName())
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

            profile.getDuelProcedure().setArena(arena);
            profile.getDuelProcedure().send();

            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
        }

    }

}
