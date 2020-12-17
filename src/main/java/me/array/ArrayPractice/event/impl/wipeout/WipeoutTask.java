package me.array.ArrayPractice.event.impl.wipeout;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class WipeoutTask extends BukkitRunnable {

	private int ticks;
	private Wipeout wipeout;
	private WipeoutState eventState;

	public WipeoutTask(Wipeout wipeout, WipeoutState eventState) {
		this.wipeout = wipeout;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.get().getWipeoutManager().getActiveWipeout() == null ||
		    !Array.get().getWipeoutManager().getActiveWipeout().equals(wipeout) || wipeout.getState() != eventState) {
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
