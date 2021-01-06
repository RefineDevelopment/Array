package me.array.ArrayPractice.event.impl.lms.task;

import me.array.ArrayPractice.event.impl.lms.LMSState;
import me.array.ArrayPractice.event.impl.lms.LMSTask;

public class LMSRoundEndTask extends LMSTask {

    public LMSRoundEndTask(me.array.ArrayPractice.event.impl.lms.LMS LMS) {
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
