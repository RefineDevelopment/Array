package xyz.refinedev.practice.events.types.spleef.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.spleef.Spleef;
import xyz.refinedev.practice.events.types.spleef.SpleefState;
import xyz.refinedev.practice.events.types.spleef.SpleefTask;

public class SpleefRoundStartTask extends SpleefTask {

	public SpleefRoundStartTask(Spleef spleef) {
		super(spleef, SpleefState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getSpleef().broadcastMessage(Locale.EVENT_STARTED.toString().replace("<event_name>", "Spleef"));
			this.getSpleef().setEventTask(null);
			this.getSpleef().setState(SpleefState.ROUND_FIGHTING);
			this.getSpleef().setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();
			this.getSpleef().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
		}
	}

}
