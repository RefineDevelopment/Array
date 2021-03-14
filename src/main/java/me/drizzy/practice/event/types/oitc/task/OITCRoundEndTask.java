package me.drizzy.practice.event.types.oitc.task;

import me.drizzy.practice.event.types.oitc.OITCState;
import me.drizzy.practice.event.types.oitc.OITCTask;

public class OITCRoundEndTask extends OITCTask {

    public OITCRoundEndTask(me.drizzy.practice.event.types.oitc.OITC OITC) {
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
