package me.array.ArrayPractice.event.impl.infected;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class InfectedTask extends BukkitRunnable {

	private int ticks;
	private Infected infected;
	private InfectedState eventState;

	public InfectedTask(Infected infected, InfectedState eventState) {
		this.infected = infected;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.get().getInfectedManager().getActiveInfected() == null ||
		    !Array.get().getInfectedManager().getActiveInfected().equals(infected) || infected.getState() != eventState) {
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
