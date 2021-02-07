package me.drizzy.practice.array.menu.menus;

import lombok.AllArgsConstructor;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageKitsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&b&lSelect a Kit to Manage";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( Kit kit : Kit.getKits() ) {
            buttons.put(buttons.size(), new KitButton(kit));
        }
        return buttons;
    }

    @AllArgsConstructor
    public static class KitButton extends Button {
        Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&7Click to edit this kit");
            return new ItemBuilder(kit.getDisplayIcon()).lore(lore).name(kit.getName()).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new ManageKitMenu(kit).openMenu(player);
        }
    }
}
