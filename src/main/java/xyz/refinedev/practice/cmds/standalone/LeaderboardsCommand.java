package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.leaderboards.menu.LeaderboardsMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Button;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/1/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class LeaderboardsCommand {

    private final Array plugin;

    @Command(name = "", desc = "Open Leaderboards Menu")
    public void leaderboard(@Sender Player player) {
        LeaderboardsMenu menu = new LeaderboardsMenu(plugin);
        menu.openMenu(player);

        Button.playSuccess(player);
        player.sendMessage(CC.translate("&7&oNow viewing leaderboards menu..."));
    }
}
