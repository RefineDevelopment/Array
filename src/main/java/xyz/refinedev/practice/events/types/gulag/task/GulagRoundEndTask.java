package xyz.refinedev.practice.events.types.gulag.task;

import xyz.refinedev.practice.events.types.gulag.Gulag;
import xyz.refinedev.practice.events.types.gulag.GulagState;
import xyz.refinedev.practice.events.types.gulag.GulagTask;

public class GulagRoundEndTask extends GulagTask {

	public GulagRoundEndTask(Gulag gulag) {
		super(gulag, GulagState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getGulag().canEnd()) {
				this.getGulag().end();
			} else {
				this.getGulag().onRound();
			}
		}
	}

}
