package me.drizzy.practice.event.types.sumo.task;

import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.event.types.sumo.SumoState;
import me.drizzy.practice.event.types.sumo.SumoTask;

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
