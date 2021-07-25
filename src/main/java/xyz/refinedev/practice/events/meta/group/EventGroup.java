package xyz.refinedev.practice.events.meta.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import xyz.refinedev.practice.events.meta.player.EventPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayerState;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/8/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class EventGroup {

    private final List<EventPlayer> players;

    private final int maxMembers;
    private final EventGroupColor color;
    private int roundWins = 0;

    private boolean eliminated = false;
    private EventPlayerState state = EventPlayerState.WAITING;

    public EventGroup(int maxMembers, EventGroupColor color) {
        this.maxMembers = maxMembers;
        this.color = color;
        this.players = new ArrayList<>();
    }

    public void addPlayer(EventTeamPlayer player) {
        player.setGroup(this);
        this.players.add(player);
    }

    public void removePlayer(EventTeamPlayer player) {
        player.setGroup(null);
        this.players.remove(player);
    }

    public int getAlivePlayers() {
        return (int) this.players.stream().filter(player -> player.getState() == EventPlayerState.WAITING).count();
    }
}
