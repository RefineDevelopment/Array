package me.drizzy.practice.events.types.parkour.task;

import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.parkour.ParkourState;
import me.drizzy.practice.events.types.parkour.ParkourTask;
import org.bukkit.entity.Player;

public class ParkourRoundEndTask extends ParkourTask {

	private Player winner;

	public ParkourRoundEndTask(Parkour parkour, Player winner) {
		super(parkour, ParkourState.ROUND_ENDING);
		this.winner = winner;
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getParkour().end(winner);
		}
	}

}
