package xyz.refinedev.practice.events.types.sumo.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.sumo.Sumo;
import xyz.refinedev.practice.events.types.sumo.SumoState;
import xyz.refinedev.practice.events.types.sumo.SumoTask;
import xyz.refinedev.practice.util.other.Cooldown;

public class SumoStartTask extends SumoTask {

	public SumoStartTask(Sumo sumo) {
		super(sumo, SumoState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getSumo().end();
			return;
		}

		if (this.getSumo().getPlayers().size() <= 1 && this.getSumo().getCooldown() != null) {
			this.getSumo().setCooldown(null);
			this.getSumo().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", "Sumo"));
		}

		if (this.getSumo().getPlayers().size() == Sumo.getMaxPlayers() || (getTicks() >= 30 && this.getSumo().getPlayers().size() >= 2)) {
			if (this.getSumo().getCooldown() == null) {
				this.getSumo().setCooldown(new Cooldown(11_000));
				this.getSumo().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", "Sumo"));
			} else {
				if (this.getSumo().getCooldown().hasExpired()) {
					this.getSumo().setState(SumoState.ROUND_STARTING);
					this.getSumo().onRound();
					this.getSumo().setTotalPlayers(this.getSumo().getPlayers().size());
					this.getSumo().setEventTask(new SumoRoundStartTask(this.getSumo()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getSumo().announce();
		}
	}

}
