package me.drizzy.practice.leaderboards.command;

import me.drizzy.practice.leaderboards.menu.LeaderboardsMenu;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"leaderboards", "lb"})
public class LeaderboardsCommand {

    public void execute(Player player) {
        new LeaderboardsMenu().openMenu(player);
    }

}
