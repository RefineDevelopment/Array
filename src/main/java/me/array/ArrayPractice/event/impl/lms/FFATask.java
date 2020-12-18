package me.array.ArrayPractice.event.impl.lms;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class FFATask extends BukkitRunnable {

	private int ticks;
	private FFA ffa;
	private FFAState eventState;

	public FFATask(FFA ffa, FFAState eventState) {
		this.ffa = ffa;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.get().getFfaManager().getActiveFFA() == null ||
		    !Array.get().getFfaManager().getActiveFFA().equals(ffa) || ffa.getState() != eventState) {
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
