package xyz.refinedev.practice.leaderboards.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.leaderboards.menu.buttons.ClanLeaderboardsButton;
import xyz.refinedev.practice.leaderboards.menu.buttons.GlobalLeaderboardsButton;
import xyz.refinedev.practice.leaderboards.menu.buttons.KitLeaderboardsButton;
import xyz.refinedev.practice.leaderboards.menu.buttons.StatisticsButton;
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
    public String getTitle(Player player) {
        return "&7Leaderboards";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(2, 1), new StatisticsButton());
        buttons.put(getSlot(4, 1), new GlobalLeaderboardsButton());
        buttons.put(getSlot(6, 1), new ClanLeaderboardsButton());

        int y = 3;
        int x = 1;

        for ( Kit kit : this.getPlugin().getKitManager().getKits()) {
            if (kit.getGameRules().isRanked() && kit.isEnabled() && !kit.equals(this.getPlugin().getKitManager().getTeamFight())) {
                buttons.put(getSlot(x++, y), new KitLeaderboardsButton(kit));
                if (x == 8) {
                    y++;
                    x=1;
                }
            }
        }
        return buttons;
    }
}
