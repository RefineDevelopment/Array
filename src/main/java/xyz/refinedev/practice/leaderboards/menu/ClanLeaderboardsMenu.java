package xyz.refinedev.practice.leaderboards.menu;

import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/3/2021
 * Project: Array
 */

public class ClanLeaderboardsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return CC.translate("&7Clan Leaderboards");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return null;
    }
}
