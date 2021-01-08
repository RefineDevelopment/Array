/*package me.array.ArrayPractice.profile.stats.menu;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class ProfileMenu extends Menu {

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&b" + target.getName() + "'s Global Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(2, new GlobalStatsButton());
        buttons.put(4, new EloStatsButton());
        buttons.put(6, new MatchStatsButton());

        return buttons;
    }

    @AllArgsConstructor
    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());

            lore.add("&7&m-------------------------------");
            lore.add(" &bELO: &r" + profile.getGlobalElo());
            lore.add(" &bLeague: &r" + profile.getEloLeague());
            lore.add("&7&m--------------------------");
            lore.add(" &bTotal Wins: &f" + profile.getTotalWins());
            lore.add(" &bTotal Losses: &f" + profile.getTotalLost());
            lore.add("&7&m-------------------------------");

            return new ItemBuilder(Material.COMPASS)
                    .name("&b&lGlobal Stats")
                    .lore(lore)
                    .build();
        }

    }

    @AllArgsConstructor
    private class EloStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7&m-------------------------------");
            lore.add("&7Left-Click to view your elo stats");
            lore.add("&7&m-------------------------------");
            return new ItemBuilder(Material.DIAMOND_SWORD)
                    .name("&b&lElo Stats")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            new ELOMenu(target).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class MatchStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&7&m-------------------------------");
            lore.add("&7Left-Click to view recent Unranked matches");
            lore.add("&7Right-Click to view recent Ranked matches");
            lore.add("&7&m-------------------------------");
            return new ItemBuilder(Material.BOOK_AND_QUILL)
                    .name("&b&lMatch History")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            if (clickType.isLeftClick()) new MatchHistoryMenu(target, false).openMenu(player);
            else new MatchHistoryMenu(target, true).openMenu(player);
        }

    }

}
*/