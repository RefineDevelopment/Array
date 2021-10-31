package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.match.MatchEndEvent;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.tournament.Tournament;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentListener implements Listener {

    private final Array plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Tournament tournament = this.plugin.getTournamentManager().getTournamentByUUID(player.getUniqueId());
        if (tournament == null) return;

        this.plugin.getTournamentManager().leaveTournament(player);
    }

    @EventHandler
    public void onMatchEndEvent(MatchEndEvent event) {
        Match match = event.getMatch();
        Tournament tournament = this.plugin.getTournamentManager().getTournamentFromMatch(match.getMatchId());
        if (tournament == null) return;

        this.plugin.getTournamentManager().removeTournamentMatch(match);
    }
}
