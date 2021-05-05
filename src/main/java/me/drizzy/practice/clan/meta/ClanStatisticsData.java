package me.drizzy.practice.clan.meta;

import lombok.Data;

@Data
public class ClanStatisticsData {

    private int elo = 1000;
    private int won = 0;
    private int lost = 0;

    public void incrementWon() {
        this.won++;
    }

    public void incrementLost() {
        this.lost++;
    }
}
