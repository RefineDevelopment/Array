package me.array.ArrayPractice.event.impl.infected.task;

import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.event.impl.infected.InfectedState;
import me.array.ArrayPractice.event.impl.infected.InfectedTask;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.scheduler.BukkitRunnable;

public class InfectedRoundStartTask extends InfectedTask {

	public InfectedRoundStartTask(Infected infected) {
		super(infected, InfectedState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getInfected().broadcastMessage(CC.AQUA + "The event has started! (Survive 10 minutes to win)");
			this.getInfected().setEventTask(null);
			this.getInfected().setState(InfectedState.ROUND_FIGHTING);

			((Infected) this.getInfected()).setRoundStart(System.currentTimeMillis());
			this.getInfected().setTask(new BukkitRunnable() {
				@Override
				public void run() {
					if (getInfected().canEnd().equalsIgnoreCase("Survivors")) getInfected().end(getInfected().canEnd());
				}
			}.runTaskTimer(Array.get(), 100L, 20L));
		} else {
			int seconds = getSeconds();

			this.getInfected().broadcastMessage("&b" + seconds + "...");
		}
	}

}
