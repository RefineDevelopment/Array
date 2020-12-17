package me.array.ArrayPractice.match.task;

import me.array.ArrayPractice.match.MatchSnapshot;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchSnapshotCleanupTask extends BukkitRunnable {

	@Override
	public void run() {
		MatchSnapshot.getSnapshots().entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getCreated() >= 45_000);
	}

}
