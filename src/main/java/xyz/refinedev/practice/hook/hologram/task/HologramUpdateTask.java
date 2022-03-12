package xyz.refinedev.practice.hook.hologram.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.hook.hologram.HologramHandler;
import xyz.refinedev.practice.hook.hologram.PracticeHologram;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class HologramUpdateTask implements Runnable {

    private final Array plugin;

    @Override
    public void run() {
        HologramHandler handler = plugin.getHologramHandler();

        for ( PracticeHologram hologram : handler.getHolograms() ) {
            if (hologram.updateIn <= 0) {
                hologram.update();
                return;
            }

            hologram.updateIn -= 1;
        }
    }
}
