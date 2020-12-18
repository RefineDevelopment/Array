package me.array.ArrayPractice.event.impl.lms.task;

import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.event.impl.lms.FFAState;
import me.array.ArrayPractice.event.impl.lms.FFATask;

public class FFARoundEndTask extends FFATask {

	public FFARoundEndTask(FFA ffa) {
		super(ffa, FFAState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getFfa().canEnd()) {
				this.getFfa().end();
			}
		}
	}

}
