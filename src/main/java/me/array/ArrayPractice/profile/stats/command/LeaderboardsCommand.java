package me.array.ArrayPractice.profile.stats.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.stats.menu.RankedLeaderboardsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"leaderboards", "lb"})
public class LeaderboardsCommand {

    public void execute(Player player) {
        new RankedLeaderboardsMenu().openMenu(player);
    }

}
