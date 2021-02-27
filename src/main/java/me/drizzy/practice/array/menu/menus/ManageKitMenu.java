package me.drizzy.practice.array.menu.menus;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageKitMenu extends Menu {

    Kit kit;

    @Override
    public String getTitle(Player player) {
        return "&bEditing " + kit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new DeleteButton());
        buttons.put(5, new ToggleButton());

        return buttons;
    }

    @ConstructorProperties({"Kit"})
    public ManageKitMenu(final Kit kit) {
        this.kit= kit;
    }

    public class DeleteButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&7Click to delete this kit");
            return new ItemBuilder(Material.INK_SACK).durability(3).name("&b&lDelete This kit").lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.performCommand("kit delete " + kit.getName());
            player.closeInventory();
            new ManageKitMenu(kit).openMenu(player);
        }
    }

    public class ToggleButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&7Click to toggle this kit");
            return new ItemBuilder(Material.INK_SACK).durability(kit.isEnabled() ? 10 : 8).name("&b&lToggle this kit").lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if(kit.isEnabled()) {
                kit.setEnabled(false);
            }
            if(!kit.isEnabled()) {
                kit.setEnabled(true);
            }
            kit.save();
            Button.playSuccess(player);
        }
    }
}