package me.array.ArrayPractice.event.impl.juggernaut.task;

import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautState;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautTask;

public class JuggernautRoundEndTask extends JuggernautTask {

	public JuggernautRoundEndTask(Juggernaut juggernaut) {
		super(juggernaut, JuggernautState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (!this.getJuggernaut().canEnd().equalsIgnoreCase("None")) {
				this.getJuggernaut().end(this.getJuggernaut().canEnd());
			}
		}
	}

}
