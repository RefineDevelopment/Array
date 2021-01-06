package me.array.ArrayPractice.event.impl.skywars.task;

import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsState;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsTask;
import me.array.ArrayPractice.util.external.Cooldown;

public class SkyWarsStartTask extends SkyWarsTask {

    public SkyWarsStartTask(SkyWars skyWars) {
        super(skyWars, SkyWarsState.WAITING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 120) {
            this.getSkyWars().end();
            return;
        }

        if (this.getSkyWars().getPlayers().size() <= 1 && this.getSkyWars().getCooldown() != null) {
            this.getSkyWars().setCooldown(null);
            this.getSkyWars().broadcastMessage("&cThere are not enough players for the skywars to start.");
        }

        if (this.getSkyWars().getPlayers().size() == this.getSkyWars().getMaxPlayers() || (getTicks() >= 3 && this.getSkyWars().getPlayers().size() >= 2)) {
            if (this.getSkyWars().getCooldown() == null) {
                this.getSkyWars().setCooldown(new Cooldown(11_000));
                this.getSkyWars().broadcastMessage("&7The skywars will start in &c10 seconds&7...");
            } else {
                if (this.getSkyWars().getCooldown().hasExpired()) {
                    this.getSkyWars().setState(SkyWarsState.ROUND_STARTING);
                    this.getSkyWars().onRound();
                    this.getSkyWars().setTotalPlayers(this.getSkyWars().getPlayers().size());
                    this.getSkyWars().setEventTask(new SkyWarsRoundStartTask(this.getSkyWars()));

                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getSkyWars().announce();
        }
    }

}
