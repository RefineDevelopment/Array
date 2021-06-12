package xyz.refinedev.practice.events.types.lms.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.lms.LMS;
import xyz.refinedev.practice.events.types.lms.LMSState;
import xyz.refinedev.practice.events.types.lms.LMSTask;

public class LMSRoundStartTask extends LMSTask {

    public LMSRoundStartTask(LMS LMS) {
        super(LMS, LMSState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getLMS().broadcastMessage(Locale.EVENT_STARTED.toString().replace("<event_name>", "LMS"));
            this.getLMS().setEventTask(null);
            this.getLMS().setState(LMSState.ROUND_FIGHTING);
            this.getLMS().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();
            this.getLMS().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
        }
    }

}
