package me.array.ArrayPractice.event.impl.skywars;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SkyWarsTask extends BukkitRunnable {

	private int ticks;
	private SkyWars skyWars;
	private SkyWarsState eventState;

	public SkyWarsTask(SkyWars skyWars, SkyWarsState eventState) {
		this.skyWars = skyWars;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.get().getSkyWarsManager().getActiveSkyWars() == null ||
		    !Array.get().getSkyWarsManager().getActiveSkyWars().equals(skyWars) || skyWars.getState() != eventState) {
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
