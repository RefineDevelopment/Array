package me.array.ArrayPractice.profile.stats.command;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.stats.menu.RankedLeaderboardsMenu;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "leaderboards" })
public class LeaderboardsCommand
{
    public void execute(final Player player) {
        Kit.getKits().forEach(Kit::updateKitLeaderboards);
        new RankedLeaderboardsMenu().openMenu(player);
    }
}
