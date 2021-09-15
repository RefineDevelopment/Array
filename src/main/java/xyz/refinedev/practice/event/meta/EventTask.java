package xyz.refinedev.practice.event.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;

@Getter
@RequiredArgsConstructor
public abstract class EventTask extends BukkitRunnable {

	private int ticks;
	private final Event event;
	private final EventState eventState;

	@Override
	public void run() {
		if (Array.getInstance().getEventManager().getActiveEvent() == null || !Array.getInstance().getEventManager().getActiveEvent().equals(this.event) || this.event.getState() != this.eventState) {
			this.cancel();
			return;
		}

		this.onRun();
		this.ticks++;
	}

	public int getSeconds() {
		return 3 - this.ticks;
	}

	public abstract void onRun();

}
