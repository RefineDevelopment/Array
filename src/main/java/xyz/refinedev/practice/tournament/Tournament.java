package xyz.refinedev.practice.tournament;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;

import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author MMC
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class Tournament {

    private final Array plugin = Array.getInstance();

    private final Set<UUID> players = new HashSet<>();
    private final Set<UUID> matches = new HashSet<>();
    private final List<TournamentTeam> aliveTeams = new ArrayList<>();
    private final Map<UUID, TournamentTeam> playerTeams = new HashMap<>();

    private final int id;
    private final int teamSize;
    private final int size;
    private final String kitName;

    private TournamentState tournamentState = TournamentState.WAITING;
    private int currentRound = 1;
    private int countdown = 31;

    public void broadcast(String message) {
        for (UUID uuid : this.players) {
            Player player = this.plugin.getServer().getPlayer(uuid);

            player.sendMessage(message);
        }
    }

    public void broadcastWithSound(String message, Sound sound) {
        for (UUID uuid : this.players) {
            Player player = this.plugin.getServer().getPlayer(uuid);

            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10, 1);
        }
    }

    public int decrementCountdown() {
        return --this.countdown;
    }

    public void addPlayer(UUID uuid) {
        this.players.add(uuid);
    }

    public void addAliveTeam(TournamentTeam team) {
        this.aliveTeams.add(team);
    }

    public void killTeam(TournamentTeam team) {
        this.aliveTeams.remove(team);
    }

    public void setPlayerTeam(UUID uuid, TournamentTeam team) {
        this.playerTeams.put(uuid, team);
    }

    public TournamentTeam getPlayerTeam(UUID uuid) {
        return this.playerTeams.get(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public void addMatch(UUID uuid) {
        this.matches.add(uuid);
    }

    public void removeMatch(UUID uuid) {
        this.matches.remove(uuid);
    }
}
