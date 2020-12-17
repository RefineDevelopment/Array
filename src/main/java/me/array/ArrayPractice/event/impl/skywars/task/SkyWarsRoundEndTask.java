package me.array.ArrayPractice.event.impl.skywars.task;

import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsState;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsTask;

public class SkyWarsRoundEndTask extends SkyWarsTask {

	public SkyWarsRoundEndTask(SkyWars skyWars) {
		super(skyWars, SkyWarsState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getSkyWars().canEnd()) {
				this.getSkyWars().end();
			}
		}
	}

}
