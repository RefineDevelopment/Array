package me.array.ArrayPractice.event.impl.infected.task;

import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.event.impl.infected.InfectedState;
import me.array.ArrayPractice.event.impl.infected.InfectedTask;

public class InfectedRoundEndTask extends InfectedTask {

	public InfectedRoundEndTask(Infected infected) {
		super(infected, InfectedState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (!this.getInfected().canEnd().equalsIgnoreCase("None")) {
				this.getInfected().end(this.getInfected().canEnd());
			}
		}
	}

}
