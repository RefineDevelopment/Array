package me.drizzy.practice.statistics.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.util.chat.CC;
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
        return "&7" + target.getName() + "'s Statistics";
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
            String elo = kit.getGameRules().isRanked() ? Integer.toString(profile.getStatisticsData().get(kit).getElo()) : "N/A";
            lore.add(CC.MENU_BAR);
            lore.add("&8 • &bELO: &f" + elo);
            lore.add("&8 • &bWins: &f" + profile.getStatisticsData().get(kit).getWon());
            lore.add("&8 • &bLosses: &f" + profile.getStatisticsData().get(kit).getLost());
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(kit.getDisplayIcon())
                    .name(kit.getDisplayName() + " &7｜ &fStats")
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
            lore.add(CC.MENU_BAR);
            lore.add("&8 • &bELO: &r" + profile.getGlobalElo());
            lore.add("&8 • &bWins: &f" + profile.getTotalWins());
            lore.add("&8 • &bLosses: &f" + profile.getTotalLost());
            lore.add(CC.MENU_BAR);
            lore.add("&8 • &bLeague: &f" + profile.getEloLeague());
            lore.add("&8 • &bW/L Ratio: &f" + profile.getWLR());
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(SkullCreator.itemFromUuid(target.getUniqueId()))
                    .name("&b&lGlobal &7｜ &fStats")
                    .lore(lore)
                    .build();
        }

    }

}
