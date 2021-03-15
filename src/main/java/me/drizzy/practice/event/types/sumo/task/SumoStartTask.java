package me.drizzy.practice.event.types.sumo.task;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.event.types.sumo.SumoState;
import me.drizzy.practice.event.types.sumo.SumoTask;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.entity.Player;

public class SumoStartTask extends SumoTask {

	public SumoStartTask(Sumo sumo) {
		super(sumo, SumoState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getSumo().end();
			return;
		}

		if (this.getSumo().getPlayers().size() <= 1 && this.getSumo().getCooldown() != null) {
			this.getSumo().setCooldown(null);
			this.getSumo().broadcastMessage("&cThere are not enough players for the sumo to start.");
		}

		if (this.getSumo().getPlayers().size() == Sumo.getMaxPlayers() || (getTicks() >= 30 && this.getSumo().getPlayers().size() >= 2)) {
			if (this.getSumo().getCooldown() == null) {
				this.getSumo().setCooldown(new Cooldown(11_000));
				this.getSumo().broadcastMessage("&7The sumo event will start in &b10 seconds&7...");
				for ( Player player : getSumo().getPlayers() ) {
					Array.getInstance().getKnockbackManager().getKnockbackType().appleKitKnockback(player, Kit.getByName("Sumo"));
				}
			} else {
				if (this.getSumo().getCooldown().hasExpired()) {
					this.getSumo().setState(SumoState.ROUND_STARTING);
					this.getSumo().onRound();
					this.getSumo().setTotalPlayers(this.getSumo().getPlayers().size());
					this.getSumo().setEventTask(new SumoRoundStartTask(this.getSumo()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getSumo().announce();
		}
	}

}
