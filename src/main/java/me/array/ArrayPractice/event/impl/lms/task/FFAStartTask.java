package me.array.ArrayPractice.event.impl.lms.task;

import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.event.impl.lms.FFAState;
import me.array.ArrayPractice.event.impl.lms.FFATask;
import me.array.ArrayPractice.util.external.Cooldown;

public class FFAStartTask extends FFATask {

	public FFAStartTask(FFA ffa) {
		super(ffa, FFAState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getFfa().end();
			return;
		}

		if (this.getFfa().getPlayers().size() <= 1 && this.getFfa().getCooldown() != null) {
			this.getFfa().setCooldown(null);
			this.getFfa().broadcastMessage("&cThere are not enough players for the ffa to start.");
		}

		if (this.getFfa().getPlayers().size() == this.getFfa().getMaxPlayers() || (getTicks() >= 30 && this.getFfa().getPlayers().size() >= 2)) {
			if (this.getFfa().getCooldown() == null) {
				this.getFfa().setCooldown(new Cooldown(11_000));
				this.getFfa().broadcastMessage("&fThe ffa will start in &b10 seconds&f...");
			} else {
				if (this.getFfa().getCooldown().hasExpired()) {
					this.getFfa().setState(FFAState.ROUND_STARTING);
					this.getFfa().onRound();
					this.getFfa().setTotalPlayers(this.getFfa().getPlayers().size());
					this.getFfa().setEventTask(new FFARoundStartTask(this.getFfa()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getFfa().announce();
		}
	}

}
