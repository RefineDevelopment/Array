package me.drizzy.practice.event.types.gulag;

import lombok.Getter;
import me.drizzy.practice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class GulagTask extends BukkitRunnable {

	private int ticks;
	private Gulag gulag;
	private GulagState eventState;

	public GulagTask(Gulag gulag, GulagState eventState) {
		this.gulag=gulag;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.getInstance().getBracketsManager().getActiveBrackets() == null ||
		    !Array.getInstance().getBracketsManager().getActiveBrackets().equals(gulag) || gulag.getState() != eventState) {
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
