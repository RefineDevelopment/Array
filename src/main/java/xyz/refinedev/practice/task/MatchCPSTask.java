package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/22/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchCPSTask extends BukkitRunnable {

    private final Array plugin;
    private final Match match;

    @Override
    public void run() {
        if (match.isEnding() || match.getTeamPlayers().isEmpty()) {
            this.cancel();
            return;
        }

        for ( TeamPlayer teamPlayer : match.getTeamPlayers() ) {
            teamPlayer.getCpsList().removeIf(count -> count < System.currentTimeMillis() - 1000L);
        }
    }
}
