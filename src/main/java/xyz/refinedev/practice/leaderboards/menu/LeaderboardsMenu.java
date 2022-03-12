package xyz.refinedev.practice.leaderboards.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.leaderboards.menu.buttons.ClanLeaderboardsButton;
import xyz.refinedev.practice.leaderboards.menu.buttons.GlobalLeaderboardsButton;
import xyz.refinedev.practice.leaderboards.menu.buttons.KitLeaderboardsButton;
import xyz.refinedev.practice.leaderboards.menu.buttons.StatisticsButton;
import xyz.refinedev.practice.managers.KitManager;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardsMenu extends Menu {

    public LeaderboardsMenu() {
        this.setAutoUpdate(true);
        this.setPlaceholder(true);
    }

    @Override
    public String getTitle(Array plugin, Player player) {
        return "&7Leaderboards";
    }
    
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(2, 1), new StatisticsButton());
        buttons.put(getSlot(4, 1), new GlobalLeaderboardsButton());
        buttons.put(getSlot(6, 1), new ClanLeaderboardsButton());

        int y = 3;
        int x = 1;

        KitManager kitManager =plugin.getKitManager();

        for ( Kit kit : kitManager.getKits()) {
            if (!kit.getGameRules().isRanked()) continue;
            if (!kit.isEnabled()) continue;
            if (kit.equals(kitManager.getTeamFight())) continue;

            buttons.put(getSlot(x++, y), new KitLeaderboardsButton(kit));
            if (x == 8) {
                y++;
                x = 1;
            }
        }
        return buttons;
    }
}
