package me.array.ArrayPractice.util;

import me.array.ArrayPractice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Practice.getInstance().getServer().getScheduler().runTaskTimer(Practice.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Practice.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), runnable, delay);
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(Practice.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), runnable);
        else
            runnable.run();
    }
}
