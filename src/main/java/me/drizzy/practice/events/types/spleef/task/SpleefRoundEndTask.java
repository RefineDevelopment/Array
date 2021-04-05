package me.drizzy.practice.events.types.spleef.task;

import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.spleef.SpleefTask;
import me.drizzy.practice.events.types.spleef.SpleefState;

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
