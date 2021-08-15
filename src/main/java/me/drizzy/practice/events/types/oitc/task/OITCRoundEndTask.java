package me.drizzy.practice.events.types.oitc.task;

import me.drizzy.practice.events.types.oitc.OITCState;
import me.drizzy.practice.events.types.oitc.OITCTask;

public class OITCRoundEndTask extends OITCTask {

    public OITCRoundEndTask(me.drizzy.practice.events.types.oitc.OITC OITC) {
        super(OITC, OITCState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getOITC().canEnd()) {
                this.getOITC().end();
            }
        }
    }

}
