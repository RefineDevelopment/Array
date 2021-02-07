package me.drizzy.practice.array.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;

import java.beans.ConstructorProperties;
import java.util.*;

public class ManageArenaMenu extends Menu {

    Arena arena;

    @Override
    public String getTitle(Player player) {
        return "&b&lYou are editing " + arena.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(2, new KitsButton());
        buttons.put(4, new TeleportButton());
        buttons.put(6, new DeleteButton());
        return buttons;
    }

    @ConstructorProperties({"Arena"})
    public ManageArenaMenu(final Arena arena) {
        this.arena = arena;
    }

    public class DeleteButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.INK_SACK).durability(3).name("&b&lDelete This Arena").lore(Arrays.asList(
                    "",
                    "&7Click to delete this Arena.",
                    "&7You will &b&lNOT &7be able to",
                    "&7recover this arena."
            )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            arena.delete();
            arena.save();
            Button.playSuccess(player);
            player.closeInventory();
            new ManageArenaMenu(arena).onOpen(player);
        }
    }

    public class KitsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.DIAMOND_SWORD).name("&b&lManage this arena's kits").lore(Arrays.asList(
                    "",
                    "&7Click to manage this arena's kits."
            )).build();

        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new ManageArenaKitsMenu(arena).openMenu(player);
            Button.playNeutral(player);
        }
    }

    public class TeleportButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.ENDER_PEARL).name("&b&lTeleport to this arena").lore(Arrays.asList(
                    "",
                    "&7Click to teleport to this arena."
            )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.teleport(arena.getSpawn1());
            Button.playSuccess(player);
            player.closeInventory();
            new ManageArenaMenu(arena).onOpen(player);
        }
    }
}
