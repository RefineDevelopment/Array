package xyz.refinedev.practice.tournament;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public enum TournamentState {

    STARTING("Starting"),
    FIGHTING("Fighting"),
    WAITING("Selecting duels"),
    ENDED("Ended");

    private final String name;
}
