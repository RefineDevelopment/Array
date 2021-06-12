package xyz.refinedev.practice.events.types.sumo.task;

import xyz.refinedev.practice.events.types.sumo.Sumo;
import xyz.refinedev.practice.events.types.sumo.SumoState;
import xyz.refinedev.practice.events.types.sumo.SumoTask;

public class SumoRoundEndTask extends SumoTask {

	public SumoRoundEndTask(Sumo sumo) {
		super(sumo, SumoState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (this.getSumo().canEnd()) {
			this.getSumo().end();
		} else {
			if (getTicks() >= 3) {
				this.getSumo().onRound();
			}
		}
	}

}
