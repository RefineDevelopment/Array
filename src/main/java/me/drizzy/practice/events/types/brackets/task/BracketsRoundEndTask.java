package me.drizzy.practice.events.types.brackets.task;

import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.brackets.BracketsState;
import me.drizzy.practice.events.types.brackets.BracketsTask;

public class BracketsRoundEndTask extends BracketsTask {

	public BracketsRoundEndTask(Brackets brackets) {
		super(brackets, BracketsState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getBrackets().canEnd()) {
				this.getBrackets().end();
			} else {
				this.getBrackets().onRound();
			}
		}
	}

}
