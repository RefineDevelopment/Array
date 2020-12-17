package me.array.ArrayPractice.event.impl.spleef.task;

import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.spleef.SpleefState;
import me.array.ArrayPractice.event.impl.spleef.SpleefTask;
import me.array.ArrayPractice.util.external.CC;

public class SpleefRoundStartTask extends SpleefTask {

	public SpleefRoundStartTask(Spleef spleef) {
		super(spleef, SpleefState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getSpleef().broadcastMessage(CC.AQUA + "The round has started!");
			this.getSpleef().setEventTask(null);
			this.getSpleef().setState(SpleefState.ROUND_FIGHTING);

			((Spleef) this.getSpleef()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getSpleef().broadcastMessage("&b" + seconds + "...");
		}
	}

}
