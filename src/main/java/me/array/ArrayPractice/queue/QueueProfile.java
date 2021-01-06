package me.array.ArrayPractice.queue;

import lombok.Data;

import java.util.UUID;

@Data
public class QueueProfile {

    private UUID playerUuid;
    private int elo;
    private int range = 25;
    private long start = System.currentTimeMillis();
    private int ticked;

    public QueueProfile(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

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

        return min < 0 ? 0 : min;
    }

    public int getMaxRange() {
        int max = this.elo + this.range;

        return max > 2500 ? 2500 : max;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof QueueProfile && ((QueueProfile) o).getPlayerUuid().equals(this.playerUuid);
    }

}
