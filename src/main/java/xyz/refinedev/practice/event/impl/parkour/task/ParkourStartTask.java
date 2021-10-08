package xyz.refinedev.practice.event.impl.parkour.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.parkour.Parkour;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerUtil;

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
        if (getTicks() >= 120) {
            this.getEvent().end();
            return;
        }

        if (this.getEvent().getPlayers().size() <= 1 && this.getEvent().getCooldown() != null) {
            this.getEvent().setCooldown(null);
            this.getEvent().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", "Parkour"));
        }

        if (this.getEvent().getPlayers().size() == this.getEvent().getMaxPlayers() || (getTicks() >= 30 && this.getEvent().getPlayers().size() >= 2)) {
            if (this.getEvent().getCooldown() == null) {
                this.getEvent().setCooldown(new Cooldown(11_000));
                this.getEvent().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", "Parkour"));
            } else {
                if (this.getEvent().getCooldown().hasExpired()) {
                    this.getEvent().setState(EventState.ROUND_STARTING);
                    this.getEvent().onRound();
                    this.getEvent().setTotalPlayers(this.getEvent().getPlayers().size());
                    this.getEvent().setEventTask(new ParkourRoundStartTask(this.getEvent()));
                    this.getEvent().getPlayers().forEach(PlayerUtil::denyMovement);
                }
            }
        }

        if (getTicks() % 10 == 0) {
            this.getEvent().announce();
        }
    }
}
