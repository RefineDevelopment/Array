package xyz.refinedev.practice.queue;

import lombok.Data;

import java.util.UUID;

@Data
public class QueueProfile {

    private final UUID uuid;
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
