package xyz.refinedev.practice.brawl;

import lombok.Data;
import java.util.UUID;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/26/2021
 * Project: Array
 */

@Data
public class BrawlPlayer {

    private final Brawl brawl;
    private final UUID uuid;
    private BrawlPlayerState state;

    private int killStreak;

    public void incrementStreak() {
        killStreak++;
    }
}
