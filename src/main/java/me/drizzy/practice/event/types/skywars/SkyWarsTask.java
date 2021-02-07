package me.drizzy.practice.event.types.skywars;

import me.drizzy.practice.Array;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SkyWarsTask extends BukkitRunnable {

    private int ticks;
    private final SkyWars skyWars;
    private final SkyWarsState eventState;

    public SkyWarsTask(SkyWars skyWars, SkyWarsState eventState) {
        this.skyWars = skyWars;
        this.eventState = eventState;
    }

    @Override
    public void run() {
        if (Array.getInstance().getSkyWarsManager().getActiveSkyWars() == null ||
                !Array.getInstance().getSkyWarsManager().getActiveSkyWars().equals(skyWars) || skyWars.getState() != eventState) {
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
