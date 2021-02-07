package me.drizzy.practice.array.menu.menus;

import lombok.AllArgsConstructor;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.ArenaType;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageArenasMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&bYou are currently editing Arenas";
    }


    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( Arena arena : Arena.getArenas() ) {
            if (!arena.isActive()) {
                buttons.put(buttons.size(), new ArenasButton(arena));
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private static class ArenasButton extends Button {

        private final Arena arena;

        @Override
        public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&bClick to manage this Arena");
        return new ItemBuilder(arena.getDisplayIcon()).name("&b&l" + arena.getName() + " " + (arena.getType().equals(ArenaType.SHARED) ? " &7(Shared)" : " &7(Standalone)")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new ManageArenaMenu(arena).openMenu(player);
        }
    }
}
