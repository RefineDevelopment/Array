package xyz.refinedev.practice.tournament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;

import java.util.*;
import java.util.stream.Stream;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/30/2021
 * Project: Array
 */

@Getter @Setter
public class TournamentTeam {

    private final Array plugin = Array.getInstance();

    private final List<UUID> players;
    private final List<UUID> alivePlayers = new ArrayList<>();
    private UUID leader;

    private final Map<UUID, String> playerNames = new HashMap<>();

    public TournamentTeam(UUID leader, List<UUID> players) {
        this.players = new ArrayList<>();
        this.leader = leader;

        for (UUID playerUUID : players) {
            this.playerNames.put(playerUUID, this.plugin.getServer().getPlayer(playerUUID).getName());
        }
    }

    public void broadcast(String message) {
        this.alivePlayers().forEach(player -> player.sendMessage(message));
    }

    public String getPlayerName(UUID playerUUID) {
        return this.playerNames.get(playerUUID);
    }
    public void killPlayer(UUID playerUUID) {
        this.alivePlayers.remove(playerUUID);
    }

    public Stream<Player> alivePlayers() {
        return this.alivePlayers.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Stream<Player> players() {
        return this.players.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }
}
