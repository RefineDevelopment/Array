package me.array.ArrayPractice.event.impl.brackets.task;

import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.brackets.BracketsState;
import me.array.ArrayPractice.event.impl.brackets.BracketsTask;

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
