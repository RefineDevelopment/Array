package me.array.ArrayPractice.event.impl.parkour.task;

import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.parkour.ParkourState;
import me.array.ArrayPractice.event.impl.parkour.ParkourTask;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;

public class ParkourRoundStartTask extends ParkourTask {

	public ParkourRoundStartTask(Parkour parkour) {
		super(parkour, ParkourState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getParkour().broadcastMessage(CC.AQUA + "The parkour has started!");
			this.getParkour().setEventTask(null);
			this.getParkour().setState(ParkourState.ROUND_FIGHTING);
			this.getParkour().getPlayers().forEach(PlayerUtil::allowMovement);

			((Parkour) this.getParkour()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getParkour().broadcastMessage("&b" + seconds + "...");
		}
	}

}
