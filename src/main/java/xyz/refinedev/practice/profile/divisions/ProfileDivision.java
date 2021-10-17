package xyz.refinedev.practice.profile.divisions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/19/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class ProfileDivision {

    private final String name;
    private String displayName = "&aDefault";
    private boolean defaultDivision;

    private int minElo = 0;
    private int maxElo = 0;

    private int xpLevel = 0;
    private int experience = 0;
}
