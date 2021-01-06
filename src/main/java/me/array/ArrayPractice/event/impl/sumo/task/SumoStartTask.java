package me.array.ArrayPractice.event.impl.sumo.task;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.event.impl.sumo.SumoState;
import me.array.ArrayPractice.event.impl.sumo.SumoTask;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.verse.jupiter.knockback.KnockbackModule;
import rip.verse.jupiter.knockback.KnockbackProfile;

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

		if (this.getSumo().getPlayers().size() == this.getSumo().getMaxPlayers() || (getTicks() >= 30 && this.getSumo().getPlayers().size() >= 2)) {
			if (this.getSumo().getCooldown() == null) {
				this.getSumo().setCooldown(new Cooldown(11_000));
				this.getSumo().broadcastMessage("&eThe sumo event will start in &610 seconds&e...");
				KnockbackProfile profile =KnockbackModule.INSTANCE.profiles.get(Practice.get().getSumoManager().getSumoKnockbackProfile());
				Player player = (Player) this.getSumo().getPlayers();
				((CraftPlayer)player).getHandle().setKnockback(profile);
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
