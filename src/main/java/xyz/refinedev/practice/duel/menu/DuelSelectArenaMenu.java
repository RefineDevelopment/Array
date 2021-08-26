package xyz.refinedev.practice.duel.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DuelSelectArenaMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Select an arena";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        Map<Integer, Button> buttons = new HashMap<>();

        for ( Arena arena : Arena.getArenas()) {
            if (!arena.isSetup()) {
                continue;
            }

                if (!arena.getKits().contains(profile.getDuelProcedure().getKit().getName())) {
                    continue;
                }

                if (profile.getDuelProcedure().getKit().getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                    continue;
                }

                if (profile.getDuelProcedure().getKit().getGameRules().isBridge() && arena.getType() == ArenaType.SHARED || profile.getDuelProcedure().getKit().getGameRules().isBridge() && arena.getType() == ArenaType.STANDALONE) {
                    continue;
                }

                if (arena.getType() == ArenaType.DUPLICATE) {
                    continue;
                }

            buttons.put(buttons.size(), new SelectArenaButton(arena));
        }

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!isClosedByMenu()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setDuelProcedure(null);
        }
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
            Profile profile = Profile.getByUuid(player.getUniqueId());

                profile.getDuelProcedure().setArena(arena);
                profile.getDuelProcedure().send();

                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

                player.closeInventory();
            }

    }

}
