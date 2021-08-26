package xyz.refinedev.practice.match.task;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.match.MatchSnapshot;

public class MatchSnapshotCleanupTask extends BukkitRunnable {

    @Override
    public void run() {
        MatchSnapshot.getSnapshots().entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getCreated() >= 60_000);
    }

}
