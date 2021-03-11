package me.drizzy.practice.event.types.oitc.task;

import me.drizzy.practice.event.types.oitc.OITC;
import me.drizzy.practice.event.types.oitc.OITCState;
import me.drizzy.practice.event.types.oitc.OITCTask;
import me.drizzy.practice.util.external.Cooldown;

public class OITCStartTask extends OITCTask {

    public OITCStartTask(OITC OITC) {
        super(OITC, OITCState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getOITC().end();
            return;
        }

        if (this.getOITC().getPlayers().size() <= 1 && this.getOITC().getCooldown() != null) {
            this.getOITC().setCooldown(null);
            this.getOITC().broadcastMessage("&cThere are not enough players for the OITC to start.");
        }

        if (this.getOITC().getPlayers().size() == OITC.getMaxPlayers() || (getTicks() >= 30 && this.getOITC().getPlayers().size() >= 2)) {
            if (this.getOITC().getCooldown() == null) {
                this.getOITC().setCooldown(new Cooldown(11_000));
                this.getOITC().broadcastMessage("&7The OITC will start in &b10 seconds&e7...");
            } else {
                if (this.getOITC().getCooldown().hasExpired()) {
                    this.getOITC().setState(OITCState.ROUND_STARTING);
                    this.getOITC().onRound();
                    this.getOITC().setTotalPlayers(this.getOITC().getPlayers().size());
                    this.getOITC().setEventTask(new OITCRoundStartTask(this.getOITC()));
                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getOITC().announce();
        }
    }

}
