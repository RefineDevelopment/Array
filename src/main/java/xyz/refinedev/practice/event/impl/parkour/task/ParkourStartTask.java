package xyz.refinedev.practice.event.impl.parkour.task;

import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/19/2021
 * Project: Array
 */

public class ParkourStartTask extends EventTask {

    public ParkourStartTask(Event event) {
        super(event, EventState.ROUND_STARTING);
    }

    @Override
    public void onRun() {

    }
}
