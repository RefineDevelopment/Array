package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.task.TournamentTask;
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

    private final Map<UUID, Integer> players = new HashMap<>();
    private final Map<UUID, Integer> matches = new HashMap<>();
    private final Map<Integer, Tournament> tournaments = new HashMap<>();

    /**
     * Create a {@link Tournament} with the proper details
     *
     * @param commandSender {@link CommandSender} the command sender creating the tournament
     * @param id            {@link Integer} the ID of the tournament
     * @param teamSize      {@link Integer} the size of total team members
     * @param size          {@link Integer} the max players allowed to play in the tournament
     * @param kitName       {@link String} the kit name which is going to be utilized in the tournament
     */
    public void createTournament(CommandSender commandSender, int id, int teamSize, int size, String kitName) {
        Tournament tournament = new Tournament(id, teamSize, size, kitName);
        this.tournaments.put(id, tournament);

        TournamentTask task = new TournamentTask(this.plugin, tournament);
        task.runTaskTimer(this.plugin, 20L, 20L);

        commandSender.sendMessage("Tournament Created");
    }

    /**
     * Have a party join the tournament
     *
     * @param id {@link Integer} the id of the tournament
     * @param player {@link Player} the leader of the party
     */
    public void joinTournament(int id, Player player) {
        Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());
        Tournament tournament = this.getTournamentById(id);
        Party party = profile.getParty();
        if (tournament == null) return;

        if (party == null) {
            this.handleJoin(tournament, player);
        } else {
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
    }

    /**
     * Have a party or player leave the tournament
     *
     * @param player {@link Player} the leader of the party
     */
    public void leaveTournament(Player player) {
        Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());
        Tournament tournament = this.getTournamentByUUID(player.getUniqueId());
        Party party = profile.getParty();
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
        Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());
        tournament.addPlayer(player.getUniqueId());

        this.players.put(player.getUniqueId(), tournament.getId());
        this.plugin.getProfileManager().teleportToSpawn(profile);

        tournament.broadcast("Joined Tournament");
    }

    /**
     * Handle a player's leave from the tournament
     *
     * @param tournament {@link Tournament} the tournament he is leaving
     * @param player {@link Player} the player leaving
     */
    private void handleLeave(Tournament tournament, Player player) {
        Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());

        TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());

        this.players.remove(player.getUniqueId());
        this.plugin.getProfileManager().teleportToSpawn(profile);

        if (PlayerUtil.checkValidity(player)) player.sendMessage("You left the tournament.");
        tournament.broadcast("Left the tournament");

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

            //String names = TeamUtil.getNames(tournamentTeam);

            this.plugin.getServer().broadcastMessage("names won the tournament");

            for ( UUID playerUUID : tournamentTeam.getAlivePlayers() ) {
                this.players.remove(playerUUID);
                Profile tournamentProfile = this.plugin.getProfileManager().getByUUID(playerUUID);
                this.plugin.getProfileManager().teleportToSpawn(tournamentProfile);
            }

            this.plugin.getTournamentManager().removeTournament(tournament.getId());
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
            Player player = this.plugin.getServer().getPlayer(playerUUID);

            tournament.removePlayer(player.getUniqueId());

            player.sendMessage(CC.RED + "You have been eliminated.");
            player.sendMessage(CC.RED + "Do /tournament status " + tournament.getId() + " to see who is left in the tournament.");

            this.players.remove(player.getUniqueId());
        }

        String word = losingTeam.getAlivePlayers().size() > 1 ? "have" : "has";

        tournament.broadcast("names eliminated by names");
    }

    /**
     * Remove a match from the tournament
     *
     * @param match {@link Match} the match being removed
     */
    public void removeTournamentMatch(Match match) {
        Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament == null) return;

        tournament.removeMatch(match.getMatchId());
        this.matches.remove(match.getMatchId());

        Team losingTeam = match.getOpponentTeam(match.getWinningTeam());
        TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getTeamPlayers().get(0).getUniqueId());

        Team winningTeam = match.getWinningTeam();
        TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAliveTeamPlayers().get(0).getUniqueId());

        tournament.killTeam(losingTournamentTeam);
        this.handleElimination(tournament, winningTournamentTeam, losingTournamentTeam);

        winningTournamentTeam.broadcast("tournament status");

        if (tournament.getMatches().size() != 0) return;
        if (tournament.getAliveTeams().size() > 1) {
            tournament.setTournamentState(TournamentState.STARTING);
            tournament.setCurrentRound(tournament.getCurrentRound() + 1);
            tournament.setCountdown(16);
            return;
        }

        //String names = TeamUtil.getNames(winningTournamentTeam);

        this.plugin.getServer().broadcastMessage("names won the Tournament !");

        for ( UUID playerUUID : winningTournamentTeam.getAlivePlayers() ) {
            this.players.remove(playerUUID);
            Profile tournamentPlayer = this.plugin.getProfileManager().getByUUID(playerUUID);
            this.plugin.getProfileManager().teleportToSpawn(tournamentPlayer);
        }

        this.plugin.getTournamentManager().removeTournament(tournament.getId());
    }

    /**
     * Is a player with this {@link UUID} in the tournament
     *
     * @param uuid {@link UUID} the uuid of the player
     * @return {@link Boolean} boolean whether they are in or not
     */
    public boolean isInTournament(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    /**
     * Get a tournament by its ID
     *
     * @param id {@link Integer} the tournament's ID
     * @return {@link Tournament} returns the queried tournament
     */
    public Tournament getTournamentById(int id) {
        return this.tournaments.get(id);
    }

    /**
     * Get a tournament from a player's {@link UUID}
     *
     * @param uuid {@link UUID} uuid of the player
     * @return {@link Tournament} queried tournament
     */
    public Tournament getTournamentByUUID(UUID uuid) {
        Integer id = this.players.get(uuid);
        if (id == null) return null;

        return this.tournaments.get(id);
    }

    /**
     * Get a tournament from a {@link Match}
     *
     * @param uuid {@link UUID} uuid of the match
     * @return {@link Tournament} queried tournament
     */
    public Tournament getTournamentFromMatch(UUID uuid) {
        Integer id = this.matches.get(uuid);
        if (id == null) return null;

        return this.tournaments.get(id);
    }

    /**
     * Eliminate the tournament from the plugin
     *
     * @param id {@link Integer} the tournament getting eliminated
     */
    public void removeTournament(Integer id) {
        Tournament tournament = this.tournaments.get(id);
        if (tournament == null) return;

        this.tournaments.remove(id);
    }

    /**
     * Add a Tournament {@link Match} to the list
     *
     * @param matchId {@link UUID} the uuid of the match
     * @param tournamentId {@link Integer} the id of the tournament
     */
    public void addTournamentMatch(UUID matchId, Integer tournamentId) {
        this.matches.put(matchId, tournamentId);
    }
}
