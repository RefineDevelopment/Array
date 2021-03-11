package me.drizzy.practice.profile.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.event.EventCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.kiteditor.menu.KitEditorSelectKitMenu;
import me.drizzy.practice.settings.SettingsMenu;
import me.drizzy.practice.statistics.menu.LeaderboardsMenu;
import me.drizzy.practice.statistics.menu.StatsMenu;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PlayerMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&bMain Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        final List<Integer> occupied = new ArrayList<>();
        final int[] taken = {11,12,13,14,15};
        for ( int take : taken ) {
            occupied.add(take);
        }
        for ( int glassslots = 0; glassslots < 27; ++glassslots ) {
            if (!occupied.contains(glassslots)) {
                buttons.put(glassslots, new GlassButton());
            }
        }

        buttons.put(11, new LeaderBoardsButton());
        buttons.put(12, new StatisticsButton());
        buttons.put(13, new SettingsButton());
        buttons.put(14, new EventButton());
        buttons.put(15, new KitEditorButton());

        return buttons;
    }

    @AllArgsConstructor
    private static class LeaderBoardsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7View every kit's top 10");
            lore.add("&7elo and global statistics");
            lore.add("");
            lore.add("&bClick to view leaderboards.");
            return new ItemBuilder(Material.ITEM_FRAME)
                    .name("&b&lLeaderboards")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new LeaderboardsMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class StatisticsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7View complete global and elo");
            lore.add("&7Statistics of your Profile");
            lore.add("");
            lore.add("&bClick to view statistics.");
            return new ItemBuilder(Material.PAPER)
                    .name("&b&lStatistics")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new StatsMenu(player).openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class SettingsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7Change your complete");
            lore.add("&7profile settings");
            lore.add("");
            lore.add("&bClick to view settings,");
            return new ItemBuilder(Material.ANVIL)
                    .name("&b&lSettings")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new SettingsMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class EventButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7Join/Host events here");
            lore.add("&7You need donator perms to host");
            lore.add("");
            lore.add("&bClick to view events menu.");
            return new ItemBuilder(Material.ENDER_PEARL)
                    .name("&b&lView Events")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            new EventCommand().execute(player);
        }

    }

    @AllArgsConstructor
    private static class KitEditorButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7Click to edit and make");
            lore.add("&7your custom kits");
            lore.add("");
            lore.add("&bClick to open kit editor menu.");
            return new ItemBuilder(Material.BOOK)
                    .name("&b&lKit Editor")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            new KitEditorSelectKitMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class GlassButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE).name("").durability(3).build();
        }
    }

}
