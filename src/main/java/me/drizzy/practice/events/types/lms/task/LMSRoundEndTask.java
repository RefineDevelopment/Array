package me.drizzy.practice.events.types.lms.task;

import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.lms.LMSState;
import me.drizzy.practice.events.types.lms.LMSTask;

public class LMSRoundEndTask extends LMSTask {

    public LMSRoundEndTask(LMS LMS) {
        super(LMS, LMSState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getLMS().canEnd()) {
                this.getLMS().end();
            }
        }
    }

}
