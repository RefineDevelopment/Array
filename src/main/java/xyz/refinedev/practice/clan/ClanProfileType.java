package xyz.refinedev.practice.clan;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 2/3/2021
 * Project: Array
 */

@Getter
@AllArgsConstructor
public enum ClanProfileType {

    LEADER(0),
    CAPTAIN(1),
    MEMBER(2);

    private final int weight;
}
