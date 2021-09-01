package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/30/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public enum EventTeamSize {

    DOUBLES(50, 100, 2),
    TRIPLES(25, 75,3),
    QUADS(15, 60, 4);

    private final int teams, maxParticipants, maxTeamPlayers;
}
