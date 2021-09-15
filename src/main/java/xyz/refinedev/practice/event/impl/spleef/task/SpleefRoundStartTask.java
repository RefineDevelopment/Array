package xyz.refinedev.practice.event.impl.spleef.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.task.EventRoundStartTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/13/2021
 * Project: Array
 */

public class SpleefRoundStartTask extends EventRoundStartTask {

    public SpleefRoundStartTask(Event event) {
        super(event);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getEvent().broadcastMessage(Locale.EVENT_STARTED.toString().replace("<event_name>", this.getEvent().getName()));
            this.getEvent().setEventTask(null);
            this.getEvent().setState(EventState.ROUND_FIGHTING);
            this.getEvent().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();
            this.getEvent().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
        }
    }

}
