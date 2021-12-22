package xyz.refinedev.practice.event.impl.brackets.solo.task;

import xyz.refinedev.practice.event.impl.brackets.solo.BracketsSolo;
import xyz.refinedev.practice.event.task.EventRoundEndTask;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

public class BracketsSoloRoundEndTask extends EventRoundEndTask {

    private final BracketsSolo event;
    
    public BracketsSoloRoundEndTask(BracketsSolo event) {
        super(event);

        this.event = event;
    }

    @Override
    public void onRun() {
        if (this.event.canEnd()) {
            this.event.handleEnd();
        } else {
            if (getTicks() >= 3) {
                this.event.onRound();
            }
        }
    }
}
