package me.drizzy.practice.event.types.skywars.task;

import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.event.types.skywars.SkyWarsState;
import me.drizzy.practice.event.types.skywars.SkyWarsTask;

public class SkyWarsRoundEndTask extends SkyWarsTask {

    public SkyWarsRoundEndTask(SkyWars skyWars) {
        super(skyWars, SkyWarsState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getSkyWars().canEnd()) {
                this.getSkyWars().end();
            }
        }
    }

}
