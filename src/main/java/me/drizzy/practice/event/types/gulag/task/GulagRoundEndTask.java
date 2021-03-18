package me.drizzy.practice.event.types.gulag.task;

import me.drizzy.practice.event.types.gulag.Gulag;
import me.drizzy.practice.event.types.gulag.GulagState;
import me.drizzy.practice.event.types.gulag.GulagTask;

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
