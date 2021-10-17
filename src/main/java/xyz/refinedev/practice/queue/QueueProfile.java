package xyz.refinedev.practice.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class QueueProfile {

    private final UUID uniqueId;
    private long start = System.currentTimeMillis();

    private int elo;
    private int range = 25;
    private int ticked;

    public void tickRange() {
        ticked++;

        if (ticked >= 3) {
            range += 3;
            ticked = 0;
        }
    }

    public boolean isInRange(int elo) {
        return elo >= (this.elo - this.range) && elo <= (this.elo + this.range);
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.start;
    }

    public int getMinRange() {
        int min = this.elo - this.range;

        return Math.max(min, 0);
    }

    public int getMaxRange() {
        int max = this.elo + this.range;

        return Math.min(max, 2500);
    }

}
