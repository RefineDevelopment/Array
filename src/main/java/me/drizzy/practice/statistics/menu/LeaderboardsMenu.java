package me.drizzy.practice.statistics.menu;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.statistics.LeaderboardsAdapter;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardsMenu extends Menu {

    public LeaderboardsMenu() {
        setAutoUpdate(true);
    }

    private static final Button BLACK_PANE = Button.placeholder(Material.STAINED_GLASS_PANE,DyeColor.BLACK.getData(),CC.translate("&7"));

    @Override
    public String getTitle(final Player player) {
        return "&7Leaderboards";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        //Place our Main Statistics Buttons
        buttons.put(getSlot(3, 1), new StatsButton());
        buttons.put(getSlot(5, 1), new GlobalLeaderboardsButton());
        //Initialize our integers
        int y = 3;
        int x = 1;
        //Place our Kit Leaderboard Buttons
        for (Kit kit : Kit.getKits()) {
            if (!kit.getGameRules().isRanked() && !kit.isEnabled()) continue;
            if (!kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                buttons.put(getSlot(x++, y), new KitLeaderboardsButton(kit));
                if (x == 8) {
                    y++;
                    x=1;
                }
            }
        }
        //Place our Glass Panes
        for (int i = 0; i < 54; i++) {
            buttons.putIfAbsent(i, BLACK_PANE);
        }
        return buttons;
    }

    @AllArgsConstructor
    private static class KitLeaderboardsButton extends Button {

        private final Kit kit;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            List<String> lore = Lists.newArrayList();
            lore.add(CC.MENU_BAR);
            int position = 1;
            for (final LeaderboardsAdapter leaderboardsAdapter : this.kit.getRankedEloLeaderboards()) {
                Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));
                if (position == 1 || position == 2 || position == 3) {
                    lore.add(" &a" + position + " &7&l| &b" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                } else {
                    lore.add(" &7" + position + " &7&l| &b" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                }
                ++position;
            }
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&b" + this.kit.getName() + " &7&l| &fTop 10").lore(lore).build();
        }
    }
    
    private static class GlobalLeaderboardsButton extends Button {

        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore =new ArrayList<>();
            int position = 1;
            lore.add(CC.MENU_BAR);
            for (final LeaderboardsAdapter leaderboardsAdapter : Profile.getGlobalEloLeaderboards()) {
                Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));
                if (position == 1 || position == 2 || position == 3) {
                    lore.add(" &a" + position + " &7&l| &b" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                } else {
                    lore.add(" &7" + position + " &7&l| &b" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                }
                ++position;
            }
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(Material.SUGAR).name("&bGlobal &7| &fTop 10").lore(lore).build();
        }
    }

    private static class StatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());

            lore.add(CC.MENU_BAR);
            for ( Kit kit : Kit.getKits() ) {
                if (kit.getGameRules().isRanked() && kit.isEnabled()) {
                lore.add("&b" + kit.getName() + ": &f" + profile.getStatisticsData().get(kit).getElo());
                }
            }
            lore.add(CC.MENU_BAR);
            lore.add("&aGlobal ELO: &f" + profile.getGlobalElo());
            lore.add("&aGlobal League: &f" + profile.getEloLeague());
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                    .name("&b&l" + player.getName() + " | Statistics")
                    .lore(lore)
                    .build();
        }
    }
}
