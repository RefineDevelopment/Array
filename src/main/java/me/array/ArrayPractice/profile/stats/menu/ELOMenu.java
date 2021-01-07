package me.array.ArrayPractice.profile.stats.menu;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ELOMenu extends Menu {

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

            lore.add("&8&m--------------------------");
            lore.add(" &b&lStats:");
            lore.add("  &fELO: &b" + elo);
            lore.add("  &fWins: &b" + wins);
            lore.add("  &fLosses: &b" + losses);
            lore.add("&8&m--------------------------");

            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&b&l" + kit.getName())
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

            lore.add("&8&m--------------------------");
            lore.add(" &b&lGlobal:");
            lore.add("  &fELO: &b" + profile.getGlobalElo());
            lore.add("  &fTotal Wins: &b" + profile.getTotalWins());
            lore.add("  &fTotal Losses: &b" + profile.getTotalLost());
            lore.add("  &fLeague: &r" + profile.getEloLeague());
            lore.add("&8&m--------------------------");

            return new ItemBuilder(Material.COMPASS)
                    .name("&b&lGlobal")
                    .lore(lore)
                    .build();
        }

    }

}
