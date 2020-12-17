package me.array.ArrayPractice.event.impl.parkour.task;

import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.parkour.ParkourState;
import me.array.ArrayPractice.event.impl.parkour.ParkourTask;
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
