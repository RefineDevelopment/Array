package me.drizzy.practice.events.types.lms.task;

import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.lms.LMSState;
import me.drizzy.practice.events.types.lms.LMSTask;
import me.drizzy.practice.util.other.Cooldown;

public class LMSStartTask extends LMSTask {

    public LMSStartTask(LMS LMS) {
        super(LMS, LMSState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getLMS().end();
            return;
        }

        if (this.getLMS().getPlayers().size() <= 1 && this.getLMS().getCooldown() != null) {
            this.getLMS().setCooldown(null);
            this.getLMS().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", "LMS"));
        }

        if (this.getLMS().getPlayers().size() == LMS.getMaxPlayers() || (getTicks() >= 30 && this.getLMS().getPlayers().size() >= 2)) {
            if (this.getLMS().getCooldown() == null) {
                this.getLMS().setCooldown(new Cooldown(11_000));
                this.getLMS().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", "LMS"));
            } else {
                if (this.getLMS().getCooldown().hasExpired()) {
                    this.getLMS().setState(LMSState.ROUND_STARTING);
                    this.getLMS().onRound();
                    this.getLMS().setTotalPlayers(this.getLMS().getPlayers().size());
                    this.getLMS().setEventTask(new LMSRoundStartTask(this.getLMS()));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getLMS().announce();
        }
    }

}
