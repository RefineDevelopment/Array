package xyz.refinedev.practice.events.types.gulag.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.gulag.Gulag;
import xyz.refinedev.practice.events.types.gulag.GulagState;
import xyz.refinedev.practice.events.types.gulag.GulagTask;
import xyz.refinedev.practice.util.other.Cooldown;

public class GulagStartTask extends GulagTask {

	public GulagStartTask(Gulag gulag) {
		super(gulag, GulagState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getGulag().end();
			return;
		}

		if (this.getGulag().getPlayers().size() <= 1 && this.getGulag().getCooldown() != null) {
			this.getGulag().setCooldown(null);
			this.getGulag().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", "Gulag"));
		}

		if (this.getGulag().getPlayers().size() == Gulag.getMaxPlayers() || (getTicks() >= 30 && this.getGulag().getPlayers().size() >= 2)) {
			if (this.getGulag().getCooldown() == null) {
				this.getGulag().setCooldown(new Cooldown(11_000));
				this.getGulag().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", "Gulag"));
			} else {
				if (this.getGulag().getCooldown().hasExpired()) {
					this.getGulag().setState(GulagState.ROUND_STARTING);
					this.getGulag().onRound();
					this.getGulag().setTotalPlayers(this.getGulag().getPlayers().size());
					this.getGulag().setEventTask(new GulagRoundStartTask(this.getGulag()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getGulag().announce();
		}
	}

}
