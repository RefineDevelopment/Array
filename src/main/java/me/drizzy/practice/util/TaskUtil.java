package me.drizzy.practice.util;

import me.drizzy.practice.Array;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        Array.getInstance().getServer().getScheduler().runTask(Array.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Array.getInstance().getServer().getScheduler().runTaskTimer(Array.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Array.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Array.getInstance().getServer().getScheduler().runTaskLater(Array.getInstance(), runnable, delay);
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(Array.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(Array.getInstance(), runnable);
        else
            runnable.run();
    }
}
