package me.array.ArrayPractice.event.impl.ffa.task;

import me.array.ArrayPractice.event.impl.ffa.FFA;
import me.array.ArrayPractice.event.impl.ffa.FFAState;
import me.array.ArrayPractice.event.impl.ffa.FFATask;

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
