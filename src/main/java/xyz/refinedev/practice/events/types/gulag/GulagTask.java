package xyz.refinedev.practice.events.types.gulag;

import lombok.Getter;
import xyz.refinedev.practice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class GulagTask extends BukkitRunnable {

	private int ticks;
	private final Gulag gulag;
	private final GulagState eventState;

	public GulagTask(Gulag gulag, GulagState eventState) {
		this.gulag=gulag;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.getInstance().getGulagManager().getActiveGulag() == null ||
		    !Array.getInstance().getGulagManager().getActiveGulag().equals(gulag) || gulag.getState() != eventState) {
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
