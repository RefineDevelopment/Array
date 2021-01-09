package me.array.ArrayPractice.event.impl.lms;

import me.array.ArrayPractice.Practice;
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
        if (Practice.getInstance().getLMSManager().getActiveLMS() == null ||
                !Practice.getInstance().getLMSManager().getActiveLMS().equals(LMS) || LMS.getState() != eventState) {
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
