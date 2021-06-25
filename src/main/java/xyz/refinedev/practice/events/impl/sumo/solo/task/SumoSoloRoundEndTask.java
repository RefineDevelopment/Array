package xyz.refinedev.practice.events.impl.sumo.solo.task;

import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.task.EventRoundEndTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

public class SumoSoloRoundEndTask extends EventRoundEndTask {

    public SumoSoloRoundEndTask(Event event) {
        super(event);
    }

    @Override
    public void onRun() {
        if (this.getEvent().canEnd()) {
            this.getEvent().end();
        } else {
            if (getTicks() >= 3) {
                this.getEvent().onRound();
            }
        }
    }
}
