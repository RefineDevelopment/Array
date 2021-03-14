package me.drizzy.practice.event.types.parkour.task;

import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.parkour.ParkourState;
import me.drizzy.practice.event.types.parkour.ParkourTask;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;

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
