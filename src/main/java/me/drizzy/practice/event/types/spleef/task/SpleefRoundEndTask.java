package me.drizzy.practice.event.types.spleef.task;

import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.spleef.SpleefTask;
import me.drizzy.practice.event.types.spleef.SpleefState;

public class SpleefRoundEndTask extends SpleefTask {

	public SpleefRoundEndTask(Spleef spleef) {
		super(spleef, SpleefState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getSpleef().canEnd()) {
				this.getSpleef().end();
			}
		}
	}

}
