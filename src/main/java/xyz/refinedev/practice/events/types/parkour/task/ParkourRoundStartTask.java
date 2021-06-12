package xyz.refinedev.practice.events.types.parkour.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.parkour.Parkour;
import xyz.refinedev.practice.events.types.parkour.ParkourState;
import xyz.refinedev.practice.events.types.parkour.ParkourTask;
import xyz.refinedev.practice.util.other.PlayerUtil;

public class ParkourRoundStartTask extends ParkourTask {

	public ParkourRoundStartTask(Parkour parkour) {
		super(parkour, ParkourState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getParkour().broadcastMessage(Locale.EVENT_STARTED.toString().replace("<event_name>", "Parkour"));
			this.getParkour().setEventTask(null);
			this.getParkour().setState(ParkourState.ROUND_FIGHTING);
			this.getParkour().getPlayers().forEach(PlayerUtil::allowMovement);
			this.getParkour().setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();
			this.getParkour().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
		}
	}

}
