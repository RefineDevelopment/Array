package me.drizzy.practice.statistics.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.Locale;
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

            Locale.STATS_KIT_LORE.toList().forEach(line -> {
                lore.add(line
                        .replace("<profile_kit_elo>", elo)
                        .replace("<profile_kit_wins>", String.valueOf(profile.getStatisticsData().get(kit).getWon()))
                        .replace("<profile_kit_losses>", String.valueOf(profile.getStatisticsData().get(kit).getLost())));
            });

            String name = Locale.STATS_KIT_HEADER.toString().replace("<kit>", kit.getDisplayName());

            return new ItemBuilder(kit.getDisplayIcon())
                    .name(name)
                    .lore(lore)
                    .build();
        }

    }

    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());

            Locale.STATS_GLOBAL_LORE.toList().forEach(line -> {
                lore.add(line
                        .replace("<profile_global_elo>", String.valueOf(profile.getGlobalElo()))
                        .replace("<profile_global_wins>", String.valueOf(profile.getTotalWins()))
                        .replace("<profile_global_losses>", String.valueOf(profile.getTotalLost()))
                        .replace("<profile_elo_division>", profile.getEloLeague())
                        .replace("<profile_wr_ratio>", String.valueOf(profile.getWLR())));

            });

            return new ItemBuilder(SkullCreator.itemFromUuid(target.getUniqueId()))
                    .name(Locale.STATS_GLOBAL_HEADER.toString())
                    .lore(lore)
                    .build();
        }

    }

}
