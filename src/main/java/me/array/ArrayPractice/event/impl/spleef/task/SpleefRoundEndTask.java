package me.array.ArrayPractice.event.impl.spleef.task;

import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.spleef.SpleefState;
import me.array.ArrayPractice.event.impl.spleef.SpleefTask;

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
