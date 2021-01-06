package me.array.ArrayPractice.profile.menu;

import me.array.ArrayPractice.event.EventCommand;
import me.array.ArrayPractice.profile.options.OptionsMenu;
import me.array.ArrayPractice.profile.stats.menu.ProfileMenu;
import me.array.ArrayPractice.profile.stats.menu.RankedLeaderboardsMenu;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PlayerMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&cOptions";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new LeaderBoardsButton());
        buttons.put(3, new StatisticsButton());
        buttons.put(5, new SettingsButton());
        buttons.put(7, new EventButton());

        return buttons;
    }

    @AllArgsConstructor
    private static class LeaderBoardsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&7Click here to go to the leaderboards");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.ITEM_FRAME)
                    .name("&bLeaderboards")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new RankedLeaderboardsMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class StatisticsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&7Click here to go to your statistics");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.PAPER)
                    .name("&bStatistics")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new ProfileMenu(player).openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class SettingsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&7Click here to go change your preferences");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.ANVIL)
                    .name("&bSettings")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new OptionsMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class EventButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&7Click here to view all events");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.ENDER_PEARL)
                    .name("&bView Events")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            new EventCommand().execute(player);
        }

    }

}
