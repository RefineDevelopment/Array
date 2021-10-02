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
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;

import java.util.UUID;

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
        Profile profile = plugin.getProfileManager().getByPlayer(event.getPlayer());
        Tournament tournament = plugin.getTournamentManager().getCurrentTournament();
        if (tournament != null) return;

        if (!profile.hasParty() && profile.isInTournament()) {
            tournament.leave(event.getPlayer());
        } else if (profile.hasParty() && profile.getParty().isInTournament()) {
            if (tournament.isFighting()) return;
            Party party = profile.getParty();
            tournament.leave(party);
        }
    }

    @EventHandler
    public void onMatchEndEvent(MatchEndEvent event) {
        Match match = event.getMatch();
        Tournament tournament = plugin.getTournamentManager().getCurrentTournament();

        if (tournament == null) return;

        if (tournament.getMatches().contains(match)) {
            if (match.isSoloMatch()) {
                Player player = match.getOpponentPlayer(match.getWinningPlayer());
                tournament.eliminateParticipant(player, match.getWinningPlayer());
            }
            if (match.isTeamMatch()) {
                UUID loserUUID = match.getOpponentTeam(match.getWinningTeam()).getLeader().getUniqueId();
                Profile loserProfile = plugin.getProfileManager().getByUUID(loserUUID);
                Party looserParty = loserProfile.getParty();

                UUID winnerUUID = match.getWinningTeam().getLeader().getUniqueId();
                Profile winnerProfile = plugin.getProfileManager().getByUUID(winnerUUID);
                Party winnerParty = winnerProfile.getParty();

                tournament.eliminateParticipant(looserParty, winnerParty);
            }

            //Remove the match
            tournament.getMatches().remove(match);
            //If matches have ended then pick last remaining team as winner
            if (tournament.getMatches().isEmpty()) {
                if (tournament.getTeamPlayers().size() == 1) {
                    tournament.end(tournament.getTeamPlayers().keySet().stream().findAny());
                    //Otherwise cancel the tournament (this shouldn't happen unless everyone leaves)
                } else if (tournament.getTeamPlayers().isEmpty()) {
                    tournament.end(null);
                } else {
                    //If the matches are not empty then move the next stage!
                    if (!tournament.getMatches().isEmpty()) {
                        tournament.nextStage();
                    }
                }
            }
        }
    }
}
