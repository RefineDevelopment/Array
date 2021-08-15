package me.drizzy.practice.events.types.brackets.task;

import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.brackets.BracketsState;
import me.drizzy.practice.events.types.brackets.BracketsTask;
import me.drizzy.practice.util.other.Cooldown;

public class BracketsStartTask extends BracketsTask {

	public BracketsStartTask(Brackets brackets) {
		super(brackets, BracketsState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getBrackets().end();
			return;
		}

		if (this.getBrackets().getPlayers().size() <= 1 && this.getBrackets().getCooldown() != null) {
			this.getBrackets().setCooldown(null);
			this.getBrackets().broadcastMessage(Locale.EVENT_NOT_ENOUGH_PLAYERS.toString().replace("<event_name>", "Brackets"));
		}

		if (this.getBrackets().getPlayers().size() == Brackets.getMaxPlayers() || (getTicks() >= 30 && this.getBrackets().getPlayers().size() >= 2)) {
			if (this.getBrackets().getCooldown() == null) {
				this.getBrackets().setCooldown(new Cooldown(11_000));
				this.getBrackets().broadcastMessage(Locale.EVENT_STARTING.toString().replace("<event_name>", "Brackets"));
			} else {
				if (this.getBrackets().getCooldown().hasExpired()) {
					this.getBrackets().setState(BracketsState.ROUND_STARTING);
					this.getBrackets().onRound();
					this.getBrackets().setTotalPlayers(this.getBrackets().getPlayers().size());
					this.getBrackets().setEventTask(new BracketsRoundStartTask(this.getBrackets()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getBrackets().announce();
		}
	}

}
