package xyz.refinedev.practice.task.other;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentState;
import xyz.refinedev.practice.tournament.TournamentTeam;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/30/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentTask extends BukkitRunnable {

    private final Array plugin;
    private final Tournament tournament;

    @Override
    public void run() {
        if (this.tournament.getTournamentState() == TournamentState.STARTING) {
            int countdown = this.tournament.decrementCountdown();

            if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
                this.tournament.broadcastWithSound("Round " + this.tournament.getCurrentRound() + " is starting in " + countdown + " seconds!", Sound.CLICK);
            }

            if (countdown == 0) {
                if (this.tournament.getCurrentRound() == 1) {
                    Set<UUID> players = Sets.newConcurrentHashSet(this.tournament.getPlayers());

                    //Making Teams for those who joined with a party
                    for (UUID player : players) {
                        Party party = this.plugin.getPartyManager().getPartyByUUID(player);

                        if (party != null) {
                            List<UUID> uuids = party.getPlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());

                            TournamentTeam team = new TournamentTeam(party.getLeader().getUniqueId(), uuids);
                            this.tournament.addAliveTeam(team);

                            for (UUID member : uuids) {
                                players.remove(member);
                                tournament.setPlayerTeam(member, team);
                            }
                        }
                    }

                    //Making teams for those who joined without a party
                    List<UUID> currentTeam = null;
                    for (UUID player : players) {
                        if (currentTeam == null) {
                            currentTeam = new ArrayList<>();
                        }

                        currentTeam.add(player);

                        if (currentTeam.size() == this.tournament.getTeamSize()) {
                            TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
                            this.tournament.addAliveTeam(team);

                            for (UUID teammate : team.getPlayers()) {
                                tournament.setPlayerTeam(teammate, team);
                            }
                            currentTeam = null;
                        }
                    }
                }

                List<TournamentTeam> teams = this.tournament.getAliveTeams();
                Collections.shuffle(teams);

                for (int i = 0; i < teams.size(); i += 2) {
                    TournamentTeam teamA = teams.get(i);

                    if (teams.size() > i + 1) {
                        TournamentTeam teamB = teams.get(i + 1);

                        for (UUID playerUUID : teamA.getAlivePlayers()) {
                            this.removeSpectator(playerUUID);
                        }
                        for (UUID playerUUID : teamB.getAlivePlayers()) {
                            this.removeSpectator(playerUUID);
                        }

                        Player teamALeader = this.plugin.getServer().getPlayer(teamA.getLeader());
                        Player teamBLeader = this.plugin.getServer().getPlayer(teamB.getLeader());

                        Team matchTeamA = new Team(new TeamPlayer(teamALeader));
                        Team matchTeamB = new Team(new TeamPlayer(teamBLeader));

                        Kit kit = this.plugin.getKitManager().getByName(this.tournament.getKitName());
                        if (kit == null) {
                            this.plugin.getTournamentManager().cancelTournament(tournament);
                            this.cancel();
                            return;
                        }

                        Arena arena = this.plugin.getArenaManager().getByKit(kit);
                        if (arena == null) {
                            this.plugin.getTournamentManager().cancelTournament(tournament);
                            this.cancel();
                            return;
                        }

                        Match match = this.plugin.getMatchManager().createTeamKitMatch(matchTeamA, matchTeamB, kit, arena);

                        this.tournament.addMatch(match.getMatchId());
                        this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), tournament.getUniqueId());

                        this.plugin.getMatchManager().start(match);
                    } else {
                        for (UUID playerUUID : teamA.getAlivePlayers()) {
                            Player player = this.plugin.getServer().getPlayer(playerUUID);

                            player.sendMessage("You were not selected this round");
                        }
                    }
                }

                StringBuilder builder = new StringBuilder();

                builder.append("Round round has started\n");
                builder.append("tournament status");

                this.tournament.broadcastWithSound(builder.toString(), Sound.FIREWORK_BLAST);
                this.tournament.setTournamentState(TournamentState.FIGHTING);
            }
        }
    }

    /**
     * Stop a profile from spectating tournament matches
     *
     * @param uuid {@link UUID} the uniqueId of the profile
     */
    public void removeSpectator(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Profile profile = this.plugin.getProfileManager().getProfileByUUID(uuid);
        Match match = profile.getMatch();
        if (player == null || !player.isOnline() || !profile.isSpectating() || match == null) return;

        this.plugin.getMatchManager().removeSpectator(match, player);
    }
}
