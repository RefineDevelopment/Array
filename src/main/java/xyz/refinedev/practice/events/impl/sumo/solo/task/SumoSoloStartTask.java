package xyz.refinedev.practice.events.impl.sumo.solo.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.task.EventStartTask;
import xyz.refinedev.practice.util.other.Cooldown;

;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

public class SumoSoloStartTask extends EventStartTask {
    
    public SumoSoloStartTask(Event event) {
        super(event);
    }
    
    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getEvent().end();
            return;
        }

        if (this.getEvent().getPlayers().size() <= 1 && this.getEvent().getCooldown() != null) {
            this.getEvent().setCooldown(null);
            this.getEvent().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", getEvent().getName()));
        }

        if (this.getEvent().getPlayers().size() == this.getEvent().getMaxPlayers() || (getTicks() >= 30 && this.getEvent().getPlayers().size() >= 2)) {
            if (this.getEvent().getCooldown() == null) {
                this.getEvent().setCooldown(new Cooldown(11_000));
                this.getEvent().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", getEvent().getName()));
            } else {
                if (this.getEvent().getCooldown().hasExpired()) {
                    this.getEvent().setState(EventState.ROUND_STARTING);
                    this.getEvent().onRound();
                    this.getEvent().setTotalPlayers(this.getEvent().getPlayers().size());
                    this.getEvent().setEventTask(new SumoSoloRoundStartTask(this.getEvent()));
                }
            }
        }

        if (getTicks() % 10 == 0) {
            this.getEvent().announce();
        }
    }
}
