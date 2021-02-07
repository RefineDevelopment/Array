package me.drizzy.practice.event.types.skywars.task;

import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.event.types.skywars.SkyWarsState;
import me.drizzy.practice.event.types.skywars.SkyWarsTask;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.Cooldown;

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
                this.getSkyWars().broadcastMessage("&7The skywars will start in &b10 seconds&7...");
            } else {
                if (this.getSkyWars().getCooldown().hasExpired()) {
                    this.getSkyWars().setState(SkyWarsState.ROUND_STARTING);
                    this.getSkyWars().onRound();
                    this.getSkyWars().setTotalPlayers(this.getSkyWars().getPlayers().size());
                    this.getSkyWars().getPlayers().forEach(PlayerUtil::denyMovement);
                    this.getSkyWars().setEventTask(new SkyWarsRoundStartTask(this.getSkyWars()));

                }
            }
        }

        if (getTicks() % 20 == 0) {
            this.getSkyWars().announce();
        }
    }

}
