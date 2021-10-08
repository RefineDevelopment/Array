package xyz.refinedev.practice.event.impl.parkour.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.task.EventRoundStartTask;
import xyz.refinedev.practice.util.other.PlayerUtil;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

public class ParkourRoundStartTask extends EventRoundStartTask {

    public ParkourRoundStartTask(Event event) {
        super(event);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getEvent().broadcastMessage(Locale.EVENT_STARTED.toString().replace("<event_name>", "Parkour"));
            this.getEvent().setEventTask(null);
            this.getEvent().setState(EventState.ROUND_FIGHTING);
            this.getEvent().getPlayers().forEach(PlayerUtil::allowMovement);
            this.getEvent().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();
            this.getEvent().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
        }
    }
}
