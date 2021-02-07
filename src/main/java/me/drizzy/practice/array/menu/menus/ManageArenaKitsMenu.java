package me.drizzy.practice.array.menu.menus;

import lombok.AllArgsConstructor;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import me.drizzy.practice.util.external.menu.button.BackButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ManageArenaKitsMenu extends Menu {

    Arena arena;

    @Override
    public String getTitle(Player player) {
        return "&b&lYou are editing " + arena.getName() + " kits";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( String kit : arena.getKits() ) {
            Kit dakit = Kit.getByName(kit);
            buttons.put(buttons.size(), new Kitbutton(dakit));
        }
        buttons.put(36,new BackButton(new ManageArenaMenu(arena)));
        return buttons;
    }

    @ConstructorProperties({"Arena"})
    public ManageArenaKitsMenu(final Arena arena) {
        this.arena = arena;
    }

    @AllArgsConstructor
    public class Kitbutton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(kit.getDisplayIcon()).name("&b&l" + kit.getName()).lore(Arrays.asList(
                    "",
                    "&bClick to remove this kit."
            )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            arena.getKits().remove(kit.getName());
            arena.save();
            player.closeInventory();
            new ManageArenaKitsMenu(arena).onOpen(player);
        }
    }
}

