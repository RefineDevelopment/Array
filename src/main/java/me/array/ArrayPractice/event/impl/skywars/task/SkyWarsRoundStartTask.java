package me.array.ArrayPractice.event.impl.skywars.task;

import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsState;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsTask;
import me.array.ArrayPractice.util.external.CC;

public class SkyWarsRoundStartTask extends SkyWarsTask {

	public SkyWarsRoundStartTask(SkyWars skyWars) {
		super(skyWars, SkyWarsState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getSkyWars().broadcastMessage(CC.AQUA + "The round has started!");
			this.getSkyWars().setEventTask(null);
			this.getSkyWars().setState(SkyWarsState.ROUND_FIGHTING);

			((SkyWars) this.getSkyWars()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getSkyWars().broadcastMessage("&b" + seconds + "...");
		}
	}

}
