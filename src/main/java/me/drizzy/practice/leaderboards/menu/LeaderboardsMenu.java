package me.drizzy.practice.leaderboards.menu;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.leaderboards.menu.buttons.GlobalLeaderboardsButton;
import me.drizzy.practice.leaderboards.menu.buttons.KitLeaderboardsButton;
import me.drizzy.practice.leaderboards.menu.buttons.StatsButton;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
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

        buttons.put(getSlot(3, 1), new StatsButton());
        buttons.put(getSlot(5, 1), new GlobalLeaderboardsButton());

        int y = 3;
        int x = 1;

        for (Kit kit : Kit.getKits()) {
            if (kit.getGameRules().isRanked() && kit.isEnabled() && !kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                buttons.put(getSlot(x++, y), new KitLeaderboardsButton(kit));
                if (x == 8) {
                    y++;
                    x=1;
                }
            }
        }

        for (int i = 0; i < 54; i++) {
            buttons.putIfAbsent(i, BLACK_PANE);
        }
        return buttons;
    }
}
