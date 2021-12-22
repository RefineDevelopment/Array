package xyz.refinedev.practice.event.impl.brackets.solo.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.brackets.solo.BracketsSolo;
import xyz.refinedev.practice.event.task.EventStartTask;
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

public class BracketsSoloStartTask extends EventStartTask {
    
    private final BracketsSolo event;
    
    public BracketsSoloStartTask(BracketsSolo event) {
        super(event);
        
        this.event = event;
    }
    
    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.event.handleEnd();
            return;
        }

        if (this.event.getPlayers().size() <= 1 && this.event.getCooldown() != null) {
            this.event.setCooldown(null);
            this.event.broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", event.getName()));
        }

        if (this.event.getPlayers().size() == this.event.getMaxPlayers() || (getTicks() >= 30 && this.event.getPlayers().size() >= 2)) {
            if (this.event.getCooldown() == null) {
                this.event.setCooldown(new Cooldown(11_000));
                this.event.broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", event.getName()));
            } else {
                if (this.event.getCooldown().hasExpired()) {
                    this.event.setState(EventState.ROUND_STARTING);
                    this.event.onRound();
                    this.event.setTotalPlayers(this.event.getPlayers().size());
                    this.event.setEventTask(new BracketsSoloRoundStartTask(this.event));
                }
            }
        }

        if (getTicks() % 10 == 0) {
            this.event.announce();
        }
    }
}
