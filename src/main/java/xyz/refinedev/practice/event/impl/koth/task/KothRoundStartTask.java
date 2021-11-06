package xyz.refinedev.practice.event.impl.koth.task;

import xyz.refinedev.practice.event.impl.koth.Koth;
import xyz.refinedev.practice.event.task.EventRoundStartTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 11/5/2021
 * Project: Array
 */

public class KothRoundStartTask extends EventRoundStartTask {

    private final Koth koth = (Koth) this.getEvent();

    public KothRoundStartTask(Koth event) {
        super(event);
    }

    @Override
    public void onRun() {

    }
}
