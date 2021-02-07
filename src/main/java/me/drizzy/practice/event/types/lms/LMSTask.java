package me.drizzy.practice.event.types.lms;

import me.drizzy.practice.Array;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class LMSTask extends BukkitRunnable {

    private int ticks;
    private final LMS LMS;
    private final LMSState eventState;

    public LMSTask(LMS LMS, LMSState eventState) {
        this.LMS = LMS;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (Array.getInstance().getLMSManager().getActiveLMS() == null ||
                !Array.getInstance().getLMSManager().getActiveLMS().equals(LMS) || LMS.getState() != eventState) {
            cancel();
            return;
        }

        onRun();

        ticks++;
    }

    public int getSeconds() {
        return 3 - ticks;
    }

    public abstract void onRun();

}
