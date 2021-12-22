package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.sumo.team.task.SumoTeamRoundStartTask;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.util.other.Cooldown;

public class EventStartTask extends EventTask {

	public EventStartTask(Event event) {
		super(event, EventState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getEvent().handleEnd();
			return;
		}

		if (this.getEvent().getPlayers().size() <= 1 && this.getEvent().getCooldown() != null) {
			this.getEvent().setCooldown(null);
			this.getEvent().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", getEvent().getName()));
		}

		if (this.getEvent().getPlayers().size() == this.getEvent().getMaxPlayers() || (getTicks() >= 30 && this.getEvent().getPlayers().size() >= (this.getEvent().isTeam() ? 6 : 4))) {
			if (this.getEvent().getCooldown() == null) {
				this.getEvent().setCooldown(new Cooldown(11_000));
				this.getEvent().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", getEvent().getName()));
			} else {
				if (this.getEvent().getCooldown().hasExpired()) {
					this.getEvent().setState(EventState.ROUND_STARTING);
					this.getEvent().onRound();
					this.getEvent().setTotalPlayers(this.getEvent().getPlayers().size());
					this.getEvent().setEventTask(new SumoTeamRoundStartTask(this.getEvent()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getEvent().announce();
		}
	}
}
