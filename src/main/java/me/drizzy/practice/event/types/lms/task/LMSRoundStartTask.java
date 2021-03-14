package me.drizzy.practice.event.types.lms.task;

import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.lms.LMSState;
import me.drizzy.practice.event.types.lms.LMSTask;
import me.drizzy.practice.util.chat.CC;

public class LMSRoundStartTask extends LMSTask {

    public LMSRoundStartTask(LMS LMS) {
        super(LMS, LMSState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getLMS().broadcastMessage(CC.GREEN + "The LMS has started!");
            this.getLMS().setEventTask(null);
            this.getLMS().setState(LMSState.ROUND_FIGHTING);

            this.getLMS().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getLMS().broadcastMessage("&f" + seconds + "...");
        }
    }

}
