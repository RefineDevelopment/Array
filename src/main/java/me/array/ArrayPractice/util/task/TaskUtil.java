package me.array.ArrayPractice.util.task;

/**

public class TaskUtil {

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

    public static void runAsync(Runnable runnable) {
        Array.getInstance().getServer().getScheduler().runTaskAsynchronously(Array.getInstance(), runnable);
    }
}
**/