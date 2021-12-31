package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerUtil;

public class EventStartTask extends EventTask {

	private final Event event;
	
	public EventStartTask(Array plugin, Event event) {
		super(plugin, event, EventState.WAITING);
		
		this.event = event;
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.event.handleEnd();
			return;
		}

		if (this.event.getPlayers().size() <= 1 && this.event.getCooldown() != null) {
			this.event.setCooldown(null);
			this.event.broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", getEvent().getName()));
		}

		if (this.event.getPlayers().size() == this.event.getMaxPlayers() || (getTicks() >= 30 && this.event.getPlayers().size() >= 2)) {
			if (this.event.getCooldown() == null) {
				this.event.setCooldown(new Cooldown(11_000));
				this.event.broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", getEvent().getName()));
			} else {
				if (this.event.getCooldown().hasExpired()) {
					this.event.setState(EventState.ROUND_STARTING);
					this.event.onRound();
					this.event.setTotalPlayers(this.event.getPlayers().size());
					this.event.setEventTask(this.event.isTeam() ? new EventTeamRoundStartTask(this.getPlugin(), this.event) : new EventRoundStartTask(this.getPlugin(), this.event));
					if (this.event.getType().equals(EventType.PARKOUR)) this.event.getPlayers().forEach(PlayerUtil::denyMovement);
				}
			}
		}

		if (this.getTicks() % 10 == 0) {
			this.event.announce();
		}
	}
}
