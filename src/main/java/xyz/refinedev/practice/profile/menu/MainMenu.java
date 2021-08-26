package xyz.refinedev.practice.profile.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.kit.kiteditor.menu.KitEditorSelectKitMenu;
import xyz.refinedev.practice.leaderboards.menu.LeaderboardsMenu;
import xyz.refinedev.practice.profile.settings.menu.SettingsMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MainMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&cMain Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new LeaderBoardsButton());
        buttons.put(12, new StatisticsButton());
        buttons.put(13, new SettingsButton());
        buttons.put(14, new EventButton());
        buttons.put(15, new KitEditorButton());

        for ( int i = 0; i < 27; i++ ) {
            buttons.putIfAbsent(i, new GlassButton());
        }
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
            lore.add("&cClick to view leaderboards.");
            return new ItemBuilder(Material.ITEM_FRAME)
                    .name("&c&lLeaderboards")
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
            lore.add("&cClick to view statistics.");
            return new ItemBuilder(Material.PAPER)
                    .name("&c&lStatistics")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            //new StatsMenu(player).openMenu(player);
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
            lore.add("&cClick to view settings,");
            return new ItemBuilder(Material.ANVIL)
                    .name("&c&lSettings")
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
            lore.add("&cClick to view events menu.");
            return new ItemBuilder(Material.ENDER_PEARL)
                    .name("&c&lView Events")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.sendMessage(CC.translate("&cEvents are currently in development!"));
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
            lore.add("&cClick to open kit editor menu.");
            return new ItemBuilder(Material.BOOK)
                    .name("&c&lKit Editor")
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
            return new ItemBuilder(Material.STAINED_GLASS_PANE).name("").durability(8).build();
        }
    }

}
