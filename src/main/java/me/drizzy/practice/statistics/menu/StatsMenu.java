package me.drizzy.practice.statistics.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.other.SkullCreator;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
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

    @AllArgsConstructor
    private class KitStatsButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            String elo = kit.getGameRules().isRanked() ? Integer.toString(profile.getStatisticsData().get(kit).getElo()) : "N/A";
            lore.add(CC.MENU_BAR);
            lore.add("&8 • &cELO: &f" + elo);
            lore.add("&8 • &cWins: &f" + profile.getStatisticsData().get(kit).getWon());
            lore.add("&8 • &cLosses: &f" + profile.getStatisticsData().get(kit).getLost());
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(kit.getDisplayIcon())
                    .name(kit.getDisplayName() + " &7｜ &fStats")
                    .lore(lore)
                    .build();
        }

    }

    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            lore.add(CC.MENU_BAR);
            lore.add("&8 • &cELO: &r" + profile.getGlobalElo());
            lore.add("&8 • &cWins: &f" + profile.getTotalWins());
            lore.add("&8 • &cLosses: &f" + profile.getTotalLost());
            lore.add(CC.MENU_BAR);
            lore.add("&8 • &cLeague: &f" + profile.getEloLeague());
            lore.add("&8 • &cW/L Ratio: &f" + profile.getWLR());
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(SkullCreator.itemFromUuid(target.getUniqueId()))
                    .name("&c&lGlobal &7｜ &fStats")
                    .lore(lore)
                    .build();
        }

    }

}
