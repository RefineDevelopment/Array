package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.MatchSnapshot;

@RequiredArgsConstructor
public class MatchSnapshotCleanupTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        plugin.getMatchManager().getSnapshotMap().entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getCreated() >= 60_000);
    }

}
