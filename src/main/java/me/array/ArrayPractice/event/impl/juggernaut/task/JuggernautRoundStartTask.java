package me.array.ArrayPractice.event.impl.juggernaut.task;

import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautState;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautTask;
import me.array.ArrayPractice.util.external.CC;

public class JuggernautRoundStartTask extends JuggernautTask {

	public JuggernautRoundStartTask(Juggernaut juggernaut) {
		super(juggernaut, JuggernautState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getJuggernaut().broadcastMessage(CC.AQUA + "The round has started!");
			this.getJuggernaut().setEventTask(null);
			this.getJuggernaut().setState(JuggernautState.ROUND_FIGHTING);

			((Juggernaut) this.getJuggernaut()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getJuggernaut().broadcastMessage("&b" + seconds + "...");
		}
	}

}
