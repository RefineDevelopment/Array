package me.drizzy.practice.event.types.oitc;

import lombok.Getter;
import me.drizzy.practice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class OITCTask extends BukkitRunnable {

    private int ticks;
    private final OITC OITC;
    private final OITCState eventState;

    public OITCTask(OITC OITC, OITCState eventState) {
        this.OITC = OITC;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (Array.getInstance().getOITCManager().getActiveOITC() == null ||
           !Array.getInstance().getOITCManager().getActiveOITC().equals(OITC) ||
            OITC.getState() != eventState) {
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
