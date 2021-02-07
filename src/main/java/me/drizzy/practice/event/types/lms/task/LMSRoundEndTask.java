package me.drizzy.practice.event.types.lms.task;

import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.lms.LMSState;
import me.drizzy.practice.event.types.lms.LMSTask;

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
