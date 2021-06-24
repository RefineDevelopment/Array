package xyz.refinedev.practice.events.task;

import me.joeleoli.nucleus.cooldown.Cooldown;
import me.joeleoli.nucleus.util.Style;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.EventTask;

public class EventStartTask extends EventTask {

	public EventStartTask(Event event) {
		super(event, EventState.WAITING);
	}

	@Override
	public void onRun() {
		if (this.getTicks() >= 600) {
			this.getEvent().end();
			return;
		}

		if (this.getEvent().getPlayers().size() <= 1 && this.getEvent().getCooldown() != null) {
			this.getEvent().setCooldown(null);
			this.getEvent().broadcastMessage(Style.YELLOW + "There are not enough players for the event to start.");
		}

		if (this.getEvent().getPlayers().size() == this.getEvent().getMaxPlayers() || (this.getTicks() >= 30 && this.getEvent().getPlayers().size() >= 2)) {
			if (this.getEvent().getCooldown() == null) {
				this.getEvent().setCooldown(new Cooldown(11_000));
				this.getEvent().broadcastMessage(Style.YELLOW + "The event will start in " + Style.PINK + "10 seconds" + Style.YELLOW + "...");
			} else {
				if (this.getEvent().getCooldown().hasExpired()) {
					this.getEvent().setState(EventState.ROUND_STARTING);
					this.getEvent().onRound();
					this.getEvent().setEventTask(new EventRoundStartTask(this.getEvent()));
				}
			}
		}

		if (this.getTicks() % 10 == 0) {
			this.getEvent().announce();
		}
	}

}
