package me.blazingtide.pistol.thread;

import lombok.AllArgsConstructor;
import me.blazingtide.pistol.Pistol;
import me.blazingtide.pistol.board.PistolBoard;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Thread responsible for updating each player's scoreboard
 * every x amount of ticks (20 ticks = 1 second)
 */
@AllArgsConstructor
public class PistolThread extends BukkitRunnable {

    private final Pistol pistol;

    public void run() {
        //Doing it in a parallel stream because there shouldn't be any slowdown for board updating if there's a large number of players
        pistol.getBoards().values().parallelStream().forEach(PistolBoard::update);
    }
}
