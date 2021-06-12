package xyz.refinedev.practice.events.types.parkour.task;

import xyz.refinedev.practice.events.types.parkour.Parkour;
import xyz.refinedev.practice.events.types.parkour.ParkourState;
import xyz.refinedev.practice.events.types.parkour.ParkourTask;
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
