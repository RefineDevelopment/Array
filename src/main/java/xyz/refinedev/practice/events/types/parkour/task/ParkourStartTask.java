package xyz.refinedev.practice.events.types.parkour.task;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.parkour.Parkour;
import xyz.refinedev.practice.events.types.parkour.ParkourState;
import xyz.refinedev.practice.events.types.parkour.ParkourTask;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.Cooldown;

public class ParkourStartTask extends ParkourTask {

	public ParkourStartTask(Parkour parkour) {
		super(parkour, ParkourState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getParkour().end(null);
			return;
		}

		if (this.getParkour().getPlayers().size() <= 1 && this.getParkour().getCooldown() != null) {
			this.getParkour().setCooldown(null);
			this.getParkour().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", "Parkour"));
		}

		if (this.getParkour().getPlayers().size() == Parkour.getMaxPlayers() || (getTicks() >= 30 && this.getParkour().getPlayers().size() >= 2)) {
			if (this.getParkour().getCooldown() == null) {
				this.getParkour().setCooldown(new Cooldown(11_000));
				this.getParkour().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", "Parkour"));
			} else {
				if (this.getParkour().getCooldown().hasExpired()) {
					this.getParkour().setState(ParkourState.ROUND_STARTING);
					this.getParkour().onRound();
					this.getParkour().setTotalPlayers(this.getParkour().getPlayers().size());
					this.getParkour().setEventTask(new ParkourRoundStartTask(this.getParkour()));
					this.getParkour().getPlayers().forEach(PlayerUtil::denyMovement);
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getParkour().announce();
		}
	}

}
