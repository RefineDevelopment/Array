package xyz.refinedev.practice.event.impl.spleef.task;

import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.task.EventRoundEndTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/13/2021
 * Project: Array
 */

public class SpleefRoundEndTask extends EventRoundEndTask {

    public SpleefRoundEndTask(Event event) {
        super(event);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            if (this.getEvent().canEnd()) {
                this.getEvent().end();
            }
        }
    }
}
