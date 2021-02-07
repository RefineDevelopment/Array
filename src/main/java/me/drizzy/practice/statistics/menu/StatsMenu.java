package me.drizzy.practice.statistics.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.SkullCreator;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsMenu extends Menu {

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&b" + target.getName() + "'s Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new GlobalStatsButton());
        for ( Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                buttons.put(buttons.size(), new KitStatsButton(kit));
            }
        }

        return buttons;
    }

    @ConstructorProperties ({"target"})
    public StatsMenu(final Player target) {
        this.target = target;
    }

    @AllArgsConstructor
    private class KitStatsButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            String elo = kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(kit).getElo()) : "N/A";
            String wins = Integer.toString(profile.getKitData().get(kit).getWon());
            String losses = Integer.toString(profile.getKitData().get(kit).getLost());

            lore.add(" &b&lStatistics");
            lore.add("  &bELO: &f" + elo);
            lore.add("  &bWins: &f" + wins);
            lore.add("  &bLosses: &f" + losses);

            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&b&l" + kit.getName() + " &7｜ &fStats")
                    .lore(lore)
                    .build();
        }

    }

    @AllArgsConstructor
    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            lore.add("");
            lore.add("&f&lStatistics");
            lore.add(" &bELO: &r" + profile.getGlobalElo());
            lore.add(" &bTotal Wins: &f" + profile.getTotalWins());
            lore.add(" &bTotal Losses: &f" + profile.getTotalLost());
            lore.add("");
            lore.add("&f&lOther");
            lore.add(" &bLeague: &f" + profile.getEloLeague());

            return new ItemBuilder(SkullCreator.itemFromUuid(target.getUniqueId()))
                    .name("&b&lGlobal &7｜ &fStats")
                    .lore(lore)
                    .build();
        }

    }

}
