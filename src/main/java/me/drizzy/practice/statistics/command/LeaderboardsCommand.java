package me.drizzy.practice.statistics.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.statistics.menu.LeaderboardsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"leaderboards", "lb"})
public class LeaderboardsCommand {

    public void execute(Player player) {
        new LeaderboardsMenu().openMenu(player);
    }

}
