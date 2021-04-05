package me.drizzy.practice.events.types.oitc.task;

import me.drizzy.practice.events.types.oitc.OITCState;
import me.drizzy.practice.events.types.oitc.OITCTask;
import me.drizzy.practice.util.chat.CC;

public class OITCRoundStartTask extends OITCTask {

    public OITCRoundStartTask(me.drizzy.practice.events.types.oitc.OITC OITC) {
        super(OITC, OITCState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getOITC().broadcastMessage(CC.GREEN + "The OITC has started!");
            this.getOITC().setEventTask(null);
            this.getOITC().setState(OITCState.ROUND_FIGHTING);

            this.getOITC().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();

            this.getOITC().broadcastMessage("&f" + seconds + "...");
        }
    }

}
