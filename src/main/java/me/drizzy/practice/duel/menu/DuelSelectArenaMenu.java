package me.drizzy.practice.duel.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
import me.drizzy.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

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
