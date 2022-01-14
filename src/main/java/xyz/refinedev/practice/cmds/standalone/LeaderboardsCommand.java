package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.leaderboards.menu.LeaderboardsMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

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
        new LeaderboardsMenu().openMenu(plugin, player);
    }
}
