package xyz.refinedev.practice.task.other;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.*;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
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
        KitManager kitManager = plugin.getKitManager();
        ArenaManager arenaManager = plugin.getArenaManager();
        TournamentManager tournamentManager = plugin.getTournamentManager();
        MatchManager matchManager = plugin.getMatchManager();

        if (this.tournament.getTournamentState() == TournamentState.STARTING) {
            int countdown = this.tournament.decrementCountdown();

            if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
                String announce = Locale.TOURNAMENT_ROUND.toString()
                        .replace("<round>", String.valueOf(tournament.getCurrentRound()))
                        .replace("<time>", String.valueOf(countdown));

                this.tournament.broadcastWithSound(announce, Sound.CLICK);
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

                        TeamPlayer teamPlayerA = new TeamPlayer(teamALeader);
                        TeamPlayer teamPlayerB = new TeamPlayer(teamBLeader);

                        Team matchTeamA = new Team(teamPlayerA);
                        Team matchTeamB = new Team(teamPlayerB);

                        Kit kit = kitManager.getByName(this.tournament.getKitName());
                        if (kit == null) {
                            tournamentManager.cancelTournament(tournament);
                            this.cancel();
                            return;
                        }

                        Arena arena = arenaManager.getByKit(kit);
                        if (arena == null) {
                            tournament.broadcast(Locale.ERROR_NO_ARENAS.toString());
                            tournamentManager.cancelTournament(tournament);
                            this.cancel();
                            return;
                        }

                        Match match = tournament.getTeamSize() >= 2 ? this.plugin.getMatchManager().createTeamKitMatch(matchTeamA, matchTeamB, kit, arena) :
                                this.plugin.getMatchManager().createSoloKitMatch(null, teamPlayerA, teamPlayerB, kit, arena, QueueType.UNRANKED);

                        this.tournament.addMatch(match.getMatchId());
                        this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), tournament.getUniqueId());

                        matchManager.start(match);
                    } else {
                        for (UUID playerUUID : teamA.getAlivePlayers()) {
                            Player player = this.plugin.getServer().getPlayer(playerUUID);
                            player.sendMessage(Locale.TOURNAMENT_SKIPPED.toString());
                        }
                    }
                }

                StringBuilder builder = new StringBuilder();

                for ( String string : Locale.TOURNAMENT_STARTED.toList() ) {
                    builder.append(string
                            .replace("<round>", String.valueOf(tournament.getCurrentRound())
                            .replace("<kit>", tournament.getKitName()))
                            .replace("<teamSize>", String.valueOf(tournament.getTeamSize())));
                }

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
        Profile profile = this.plugin.getProfileManager().getProfile(uuid);
        Match match = profile.getMatch();
        if (player == null || !player.isOnline() || !profile.isSpectating() || match == null) return;

        this.plugin.getMatchManager().removeSpectator(match, player);
    }
}
