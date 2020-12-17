package me.array.ArrayPractice.util;

import me.array.ArrayPractice.Array;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void run(Runnable runnable) {
        Array.get().getServer().getScheduler().runTask(Array.get(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Array.get().getServer().getScheduler().runTaskTimer(Array.get(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Array.get(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Array.get().getServer().getScheduler().runTaskLater(Array.get(), runnable, delay);
    }

    public static void runSync(Runnable runnable) {
        if(Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(Array.get(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if(Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(Array.get(), runnable);
        else
            runnable.run();
    }
}
