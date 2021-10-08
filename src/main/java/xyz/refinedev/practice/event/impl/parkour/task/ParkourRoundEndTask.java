package xyz.refinedev.practice.event.impl.parkour.task;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.task.EventRoundEndTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

public class ParkourRoundEndTask extends EventRoundEndTask {

    public ParkourRoundEndTask(Event event) {
        super(event);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getEvent().end();
        }
    }
}
