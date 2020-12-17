

package me.array.ArrayPractice.queue;

import java.util.UUID;

public class QueueProfile
{
    private UUID playerUuid;
    private int elo;
    private int range;
    private long start;
    private int ticked;
    
    public QueueProfile(final UUID playerUuid) {
        this.range = 25;
        this.start = System.currentTimeMillis();
        this.playerUuid = playerUuid;
    }
    
    public void tickRange() {
        ++this.ticked;
        if (this.ticked >= 3) {
            this.range += 3;
            this.ticked = 0;
        }
    }
    
    public boolean isInRange(final int elo) {
        return elo >= this.elo - this.range && elo <= this.elo + this.range;
    }
    
    public long getPassed() {
        return System.currentTimeMillis() - this.start;
    }
    
    public int getMinRange() {
        final int min = this.elo - this.range;
        return (min < 0) ? 0 : min;
    }
    
    public int getMaxRange() {
        final int max = this.elo + this.range;
        return (max > 2500) ? 2500 : max;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof QueueProfile && ((QueueProfile)o).getPlayerUuid().equals(this.playerUuid);
    }
    
    public UUID getPlayerUuid() {
        return this.playerUuid;
    }
    
    public int getElo() {
        return this.elo;
    }
    
    public int getRange() {
        return this.range;
    }
    
    public long getStart() {
        return this.start;
    }
    
    public int getTicked() {
        return this.ticked;
    }
    
    public void setPlayerUuid(final UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
    
    public void setElo(final int elo) {
        this.elo = elo;
    }
    
    public void setRange(final int range) {
        this.range = range;
    }
    
    public void setStart(final long start) {
        this.start = start;
    }
    
    public void setTicked(final int ticked) {
        this.ticked = ticked;
    }
    
    @Override
    public String toString() {
        return "QueueProfile(playerUuid=" + this.getPlayerUuid() + ", elo=" + this.getElo() + ", range=" + this.getRange() + ", start=" + this.getStart() + ", ticked=" + this.getTicked() + ")";
    }
}
