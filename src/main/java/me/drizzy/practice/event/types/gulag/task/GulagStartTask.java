package me.drizzy.practice.event.types.gulag.task;

import me.drizzy.practice.event.types.gulag.Gulag;
import me.drizzy.practice.event.types.gulag.GulagState;
import me.drizzy.practice.event.types.gulag.GulagTask;
import me.drizzy.practice.util.external.Cooldown;

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
			this.getGulag().broadcastMessage("&cThere are not enough players for the brackets to start.");
		}

		if (this.getGulag().getPlayers().size() == this.getGulag().getMaxPlayers() || (getTicks() >= 30 && this.getGulag().getPlayers().size() >= 2)) {
			if (this.getGulag().getCooldown() == null) {
				this.getGulag().setCooldown(new Cooldown(11_000));
				this.getGulag().broadcastMessage("&fThe brackets will start in &b10 seconds&f...");
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
