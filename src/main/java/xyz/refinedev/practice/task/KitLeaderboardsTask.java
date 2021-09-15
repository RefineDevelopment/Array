package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/6/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class KitLeaderboardsTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        if (!plugin.isEnabled()) {
            cancel();
            return;
        }
        Kit.getKits().forEach(plugin.getLeaderboardsManager()::loadKitLeaderboards);
    }
}
