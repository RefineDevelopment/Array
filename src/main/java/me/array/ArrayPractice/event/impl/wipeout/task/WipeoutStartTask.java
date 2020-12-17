package me.array.ArrayPractice.event.impl.wipeout.task;

import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutState;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutTask;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.Cooldown;

public class WipeoutStartTask extends WipeoutTask {

	public WipeoutStartTask(Wipeout wipeout) {
		super(wipeout, WipeoutState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getWipeout().end(null);
			return;
		}

		if (this.getWipeout().getPlayers().size() <= 1 && this.getWipeout().getCooldown() != null) {
			this.getWipeout().setCooldown(null);
			this.getWipeout().broadcastMessage("&cThere are not enough players for the wipeout to start.");
		}

		if (this.getWipeout().getPlayers().size() == this.getWipeout().getMaxPlayers() || (getTicks() >= 5 && this.getWipeout().getPlayers().size() >= 2)) {
			if (this.getWipeout().getCooldown() == null) {
				this.getWipeout().setCooldown(new Cooldown(11_000));
				this.getWipeout().broadcastMessage("&fThe wipeout will start in &b10 seconds&f...");
			} else {
				if (this.getWipeout().getCooldown().hasExpired()) {
					this.getWipeout().setState(WipeoutState.ROUND_STARTING);
					this.getWipeout().onRound();
					this.getWipeout().setTotalPlayers(this.getWipeout().getPlayers().size());
					this.getWipeout().setEventTask(new WipeoutRoundStartTask(this.getWipeout()));
					this.getWipeout().getPlayers().forEach(PlayerUtil::denyMovement);
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getWipeout().announce();
		}
	}

}
