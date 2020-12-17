package me.array.ArrayPractice.event.impl.juggernaut.task;

import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautState;
import me.array.ArrayPractice.event.impl.juggernaut.JuggernautTask;
import me.array.ArrayPractice.event.impl.juggernaut.player.JuggernautPlayer;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.Cooldown;

public class JuggernautStartTask extends JuggernautTask {

	public JuggernautStartTask(Juggernaut juggernaut) {
		super(juggernaut, JuggernautState.WAITING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 120) {
			this.getJuggernaut().end("None");
			return;
		}

		if (this.getJuggernaut().getPlayers().size() <= 1 && this.getJuggernaut().getCooldown() != null) {
			this.getJuggernaut().setCooldown(null);
			this.getJuggernaut().broadcastMessage("&cThere are not enough players for the juggernaut to start.");
		}

		if (this.getJuggernaut().getPlayers().size() == this.getJuggernaut().getMaxPlayers() || (getTicks() >= 30 && this.getJuggernaut().getPlayers().size() >= 2)) {
			if (this.getJuggernaut().getCooldown() == null) {
				this.getJuggernaut().setCooldown(new Cooldown(11_000));
				this.getJuggernaut().broadcastMessage("&fThe juggernaut will start in &b10 seconds&f...");
			} else {
				if (this.getJuggernaut().getCooldown().hasExpired()) {
					this.getJuggernaut().setState(JuggernautState.ROUND_STARTING);
					JuggernautPlayer temp = this.getJuggernaut().getEventPlayer(this.getJuggernaut().getRemainingPlayers().get(this.getJuggernaut().getRandomPlayer()));
					temp.setJuggernaut(true);
					temp.setKit(Kit.getByName("NoDebuff"));
					this.getJuggernaut().setJuggernaut(temp);
					this.getJuggernaut().onRound();
					this.getJuggernaut().setTotalPlayers(this.getJuggernaut().getPlayers().size());
					this.getJuggernaut().setEventTask(new JuggernautRoundStartTask(this.getJuggernaut()));
				}
			}
		}

		if (getTicks() % 10 == 0) {
			this.getJuggernaut().announce();
		}
	}

}
