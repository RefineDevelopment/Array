package xyz.refinedev.practice.events;

import lombok.Getter;
import me.joeleoli.praxi.Praxi;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class EventTask extends BukkitRunnable {

	private int ticks;
	private Event event;
	private EventState eventState;

	public EventTask(Event event, EventState eventState) {
		this.event = event;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Praxi.getInstance().getEventManager().getActiveEvent() == null || !Praxi.getInstance().getEventManager().getActiveEvent().equals(this.event) || this.event.getState() != this.eventState) {
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
