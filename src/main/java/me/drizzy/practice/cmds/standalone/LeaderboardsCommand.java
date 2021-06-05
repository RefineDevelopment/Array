package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.leaderboards.menu.LeaderboardsMenu;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/1/2021
 * Project: Array
 */

public class LeaderboardsCommand {

    @Command(name = "", desc = "Open Leaderboards Menu")
    public void leaderboard(@Sender Player player) {
        new LeaderboardsMenu().openMenu(player);
    }
}
