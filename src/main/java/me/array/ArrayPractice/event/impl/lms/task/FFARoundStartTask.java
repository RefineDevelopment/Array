package me.array.ArrayPractice.event.impl.lms.task;

import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.event.impl.lms.FFAState;
import me.array.ArrayPractice.event.impl.lms.FFATask;
import me.array.ArrayPractice.util.external.CC;

public class FFARoundStartTask extends FFATask {

	public FFARoundStartTask(FFA ffa) {
		super(ffa, FFAState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getFfa().broadcastMessage(CC.AQUA + "The round has started!");
			this.getFfa().setEventTask(null);
			this.getFfa().setState(FFAState.ROUND_FIGHTING);

			((FFA) this.getFfa()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getFfa().broadcastMessage("&b" + seconds + "...");
		}
	}

}
