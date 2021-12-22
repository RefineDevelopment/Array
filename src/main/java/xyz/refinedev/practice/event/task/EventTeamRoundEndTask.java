package xyz.refinedev.practice.event.task;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 12/22/2021
 * Project: Array
 */

public class EventTeamRoundEndTask extends EventTask {

    public EventTeamRoundEndTask(Array plugin, Event event) {
        super(plugin, event, EventState.ROUND_ENDING);
    }

    @Override
    public void onRun() {
        if (this.getEvent().canEnd()) {
            this.getEvent().handleEnd();
        } else {
            if (getTicks() >= 3) {
                this.getEvent().onRound();
            }
        }
    }
}
