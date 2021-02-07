package me.drizzy.practice.event.types.brackets.task;

import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.brackets.BracketsState;
import me.drizzy.practice.event.types.brackets.BracketsTask;

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
