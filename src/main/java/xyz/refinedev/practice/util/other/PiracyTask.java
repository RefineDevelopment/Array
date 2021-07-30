package xyz.refinedev.practice.util.other;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */

public class PiracyTask extends BukkitRunnable {

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        new PiracyMeta(Array.getInstance(), Array.getInstance().getConfigHandler().getLICENSE()).hiddenVerify();
    }
}
