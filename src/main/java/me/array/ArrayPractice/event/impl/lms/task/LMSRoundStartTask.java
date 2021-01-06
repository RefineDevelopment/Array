package me.array.ArrayPractice.event.impl.lms.task;

import me.array.ArrayPractice.event.impl.lms.LMSState;
import me.array.ArrayPractice.event.impl.lms.LMSTask;
import me.array.ArrayPractice.util.external.CC;

public class LMSRoundStartTask extends LMSTask {

    public LMSRoundStartTask(me.array.ArrayPractice.event.impl.lms.LMS LMS) {
        super(LMS, LMSState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getLMS().broadcastMessage(CC.RED + "The round has started!");
            this.getLMS().setEventTask(null);
            this.getLMS().setState(LMSState.ROUND_FIGHTING);

            this.getLMS().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getLMS().broadcastMessage("&b" + seconds + "&...");
        }
    }

}
