package me.drizzy.practice.event.types.spleef.task;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.spleef.SpleefState;
import me.drizzy.practice.event.types.spleef.SpleefTask;

public class SpleefRoundStartTask extends SpleefTask {

	public SpleefRoundStartTask(Spleef spleef) {
		super(spleef, SpleefState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getSpleef().broadcastMessage(CC.AQUA + "The round has started!");
			this.getSpleef().setEventTask(null);
			this.getSpleef().setState(SpleefState.ROUND_FIGHTING);

			((Spleef) this.getSpleef()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getSpleef().broadcastMessage("&b" + seconds + "...");
		}
	}

}
