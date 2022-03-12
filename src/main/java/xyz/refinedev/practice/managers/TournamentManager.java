package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.task.other.TournamentTask;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentState;
import xyz.refinedev.practice.tournament.TournamentTeam;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/28/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class TournamentManager {

    private final Array plugin;

    private final Map<UUID, UUID> matches = new HashMap<>();
    private final Map<UUID, Tournament> tournaments = new HashMap<>();
    private final Map<Tournament, Integer> tasks = new HashMap<>();

    /**
     * Create a {@link Tournament} with the proper details
     *
     * @param sender {@link CommandSender} the command sender creating the tournament
     * @param teamSize      {@link Integer} the size of total team members
     * @param size          {@link Integer} the max players allowed to play in the tournament
     * @param kitName       {@link String} the kit name which is going to be utilized in the tournament
     */
    public void createTournament(CommandSender sender, int teamSize, int size, String kitName) {
        Tournament tournament = new Tournament(UUID.randomUUID(), teamSize, size, kitName);
        this.tournaments.put(tournament.getUniqueId(), tournament);

        TournamentTask task = new TournamentTask(this.plugin, tournament);
        task.runTaskTimer(this.plugin, 20L, 20L);
        this.tasks.put(tournament, task.getTaskId());

        sender.sendMessage(ChatColor.GREEN + "Successfully created " + teamSize + "v" + teamSize + " " + kitName + " tournament. (Max players: " + size + ")");
    }

    /**
     * Have a party join the tournament
     *
     * @param id {@link UUID} the id of the tournament
     * @param player {@link Player} the leader of the party
     */
    public void joinTournament(UUID id, Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        Tournament tournament = this.getTournamentById(id);
        if (tournament == null) return;

        if (party == null) {
            this.handleJoin(tournament, player);
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }

        if (party.getPlayers().size() + tournament.getPlayers().size() > tournament.getSize()) {
            player.sendMessage(CC.RED + "This tournament is full!");
            return;
        }

        if (party.getPlayers().size() != tournament.getTeamSize() || party.getPlayers().size() == 1) {
            player.sendMessage(CC.RED + "You are in a party that does not match this tournament's description!");
        }

        for ( Player partyPlayer : party.getPlayers() ) {
            this.handleJoin(tournament, partyPlayer);
        }

        if (tournament.getPlayers().size() == tournament.getSize()) {
            tournament.setTournamentState(TournamentState.STARTING);
        }
    }

    /**
     * Have a party or player leave the tournament
     *
     * @param player {@link Player} the leader of the party
     */
    public void leaveTournament(Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        PartyManager partyManager = this.plugin.getPartyManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());

        Tournament tournament = this.getTournamentByUUID(player.getUniqueId());
        if (tournament == null) return;

        if (party == null || tournament.getTournamentState() == TournamentState.FIGHTING) {
            this.handleLeave(tournament, player);
        } else {
            if (!party.isLeader(player.getUniqueId())) {
                player.sendMessage(Locale.PARTY_NOTLEADER.toString());
                return;
            }
            for (Player partyMembers : party.getPlayers()) {
                this.handleLeave(tournament, partyMembers);
            }
        }
    }

    /**
     * Have the player join the tournament
     *
     * @param tournament {@link Tournament} the tournament joining
     * @param player {@link Player} the player joining
     */
    private void handleJoin(Tournament tournament, Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        profile.setTournament(tournament.getUniqueId());

        tournament.addPlayer(player.getUniqueId());
        tournament.broadcast("Joined Tournament");

        profileManager.teleportToSpawn(profile);
    }

    /**
     * Handle a player's leave from the tournament
     *
     * @param tournament {@link Tournament} the tournament he is leaving
     * @param player {@link Player} the player leaving
     */
    private void handleLeave(Tournament tournament, Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        profile.setTournament(null);

        TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());
        profileManager.teleportToSpawn(profile);

        tournament.broadcast(Locale.TOURNAMENT_PLAYER_LEAVE.toString());

        if (team == null) return;
        team.killPlayer(player.getUniqueId());

        if (team.getAlivePlayers().size() != 0) {
            if (team.getLeader().equals(player.getUniqueId())) {
                team.setLeader(team.getAlivePlayers().get(0));
            }
            return;
        }

        tournament.killTeam(team);
        if (tournament.getAliveTeams().size() == 1) {
            TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);

            plugin.getServer().broadcastMessage(CC.translate(Locale.TOURNAMENT_WON.toString())
                    .replace("<winners>", tournamentTeam.getNames())
                    .replace("<kit>", tournament.getKitName())
                    .replace("<teamSize>", String.valueOf(tournament.getTeamSize())));

            for ( UUID playerUUID : tournamentTeam.getAlivePlayers() ) {
                Profile tournamentProfile = this.plugin.getProfileManager().getProfile(playerUUID);
                tournamentProfile.setTournament(null);

                profileManager.teleportToSpawn(tournamentProfile);
            }

            this.removeTournament(tournament.getUniqueId());
        }

    }

    /**
     * Handle the elimination of a team from the match
     *
     * @param tournament {@link Tournament} the tournament
     * @param winnerTeam {@link TournamentTeam} the winning team
     * @param losingTeam {@link TournamentTeam} the losing team
     */
    private void handleElimination(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
        for (UUID playerUUID : losingTeam.getAlivePlayers()) {
            Profile profile = this.plugin.getProfileManager().getProfile(playerUUID);
            Player player = this.plugin.getServer().getPlayer(playerUUID);

            tournament.removePlayer(player.getUniqueId());

            player.sendMessage(Locale.TOURNAMENT_ELIMINATED.toString());
            profile.setTournament(null);
        }

        String soloAnnounce = CC.translate(Locale.TOURNAMENT_PLAYER_ELIMINATED.toString())
                .replace("<playerA>", losingTeam.getLeaderName())
                .replace("<playerB>", winnerTeam.getLeaderName());

        String teamAnnounce = CC.translate(Locale.TOURNAMENT_TEAM_ELIMINATED.toString())
                .replace("<playerA>", losingTeam.getLeaderName())
                .replace("<playerB>", winnerTeam.getLeaderName());

        String alive = CC.translate(Locale.TOURNAMENT_REMAINING.toString())
                .replace("<players>", String.valueOf(tournament.getPlayers().size()))
                .replace("<maxPlayers>", String.valueOf(tournament.getSize()));

        tournament.broadcast(tournament.getTeamSize() >= 2 ? teamAnnounce : soloAnnounce);
        tournament.broadcast(alive);
    }

    /**
     * Remove a match from the tournament
     *
     * @param match {@link Match} the match being removed
     */
    public void removeTournamentMatch(Match match) {
        ProfileManager profileManager = plugin.getProfileManager();
        TournamentManager tournamentManager = plugin.getTournamentManager();

        Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament == null) return;

        tournament.removeMatch(match.getMatchId());
        this.matches.remove(match.getMatchId());

        Team losingTeam = match.getOpponentTeam(match.getWinningTeam());
        TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getTeamPlayers().get(0).getUniqueId());

        Team winningTeam = match.getWinningTeam();
        TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAliveTeamPlayers().get(0).getUniqueId());

        if (losingTournamentTeam != null) {
            tournament.killTeam(losingTournamentTeam);
            this.handleElimination(tournament, winningTournamentTeam, losingTournamentTeam);
        }

        if (tournament.getMatches().size() != 0) return;
        if (tournament.getAliveTeams().size() > 1) {
            tournament.setTournamentState(TournamentState.STARTING);
            tournament.setCurrentRound(tournament.getCurrentRound() + 1);
            tournament.setCountdown(11);
            return;
        }

        plugin.getServer().broadcastMessage(CC.translate(Locale.TOURNAMENT_WON.toString())
                .replace("<winners>", winningTournamentTeam.getNames())
                .replace("<kit>", tournament.getKitName())
                .replace("<teamSize>", String.valueOf(tournament.getTeamSize())));

        for ( UUID playerUUID : winningTournamentTeam.getAlivePlayers() ) {
            Profile profile = profileManager.getProfile(playerUUID);
            profile.setTournament(null);

            profileManager.teleportToSpawn(profile);
        }

        tournamentManager.removeTournament(tournament.getUniqueId());
    }

    /**
     * Cancel the specified tournament (also the job of twitter)
     *
     * @param tournament {@link Tournament} the tournament getting cancelled
     */
    public void cancelTournament(Tournament tournament) {
        ProfileManager profileManager = plugin.getProfileManager();
        MatchManager matchManager = plugin.getMatchManager();

        this.plugin.getServer().broadcastMessage(Locale.TOURNAMENT_CANCELLED.toString());

        for (UUID uuid : tournament.getPlayers()) {
            Profile profile = profileManager.getProfile(uuid);
            if (profile.isInFight()) {
                Match match = profile.getMatch();
                matchManager.end(match);
            }
            profileManager.teleportToSpawn(profile);
        }

        tournament.getPlayers().clear();
        tournament.getMatches().clear();

        this.tournaments.remove(tournament.getUniqueId());

        Integer id = this.tasks.get(tournament);
        if (id == null) return;

        this.plugin.getServer().getScheduler().cancelTask(id);
    }

    /**
     * Get a tournament by its ID
     *
     * @param id {@link UUID} the tournament's ID
     * @return {@link Tournament} returns the queried tournament
     */
    public Tournament getTournamentById(UUID id) {
        return this.tournaments.get(id);
    }

    /**
     * Get a tournament from a player's {@link UUID}
     *
     * @param uuid {@link UUID} uniqueId of the player
     * @return {@link Tournament} queried tournament
     */
    public Tournament getTournamentByUUID(UUID uuid) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(uuid);
        UUID id = profile.getTournament();
        if (id == null) return null;

        return this.tournaments.get(id);
    }

    /**
     * Get a tournament from a {@link Match}
     *
     * @param uuid {@link UUID} uniqueId of the match
     * @return {@link Tournament} queried tournament
     */
    public Tournament getTournamentFromMatch(UUID uuid) {
        UUID id = this.matches.get(uuid);
        if (id == null) return null;

        return this.tournaments.get(id);
    }

    /**
     * Eliminate the tournament from the plugin
     *
     * @param id {@link UUID} the tournament getting eliminated
     */
    public void removeTournament(UUID id) {
        Tournament tournament = this.tournaments.get(id);
        if (tournament == null) return;

        this.tournaments.remove(id);
    }

    /**
     * Add a Tournament {@link Match} to the list
     *
     * @param matchId {@link UUID} the uniqueId of the match
     * @param tournamentId {@link UUID} the id of the tournament
     */
    public void addTournamentMatch(UUID matchId, UUID tournamentId) {
        this.matches.put(matchId, tournamentId);
    }
}
