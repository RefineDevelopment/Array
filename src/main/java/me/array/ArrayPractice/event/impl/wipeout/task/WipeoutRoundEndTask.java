package me.array.ArrayPractice.event.impl.wipeout.task;

import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutState;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutTask;
import org.bukkit.entity.Player;

public class WipeoutRoundEndTask extends WipeoutTask {

	private Player winner;

	public WipeoutRoundEndTask(Wipeout wipeout, Player winner) {
		super(wipeout, WipeoutState.ROUND_ENDING);
		this.winner = winner;
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getWipeout().end(winner);
		}
	}

}
