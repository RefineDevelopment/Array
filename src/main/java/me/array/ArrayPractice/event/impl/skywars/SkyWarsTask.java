package me.array.ArrayPractice.event.impl.skywars;

import me.array.ArrayPractice.Practice;
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
        if (Practice.get().getSkyWarsManager().getActiveSkyWars() == null ||
                !Practice.get().getSkyWarsManager().getActiveSkyWars().equals(skyWars) || skyWars.getState() != eventState) {
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
