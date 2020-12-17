package me.array.ArrayPractice.event.impl.juggernaut;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class JuggernautTask extends BukkitRunnable {

	private int ticks;
	private Juggernaut juggernaut;
	private JuggernautState eventState;

	public JuggernautTask(Juggernaut juggernaut, JuggernautState eventState) {
		this.juggernaut = juggernaut;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.get().getJuggernautManager().getActiveJuggernaut() == null ||
		    !Array.get().getJuggernautManager().getActiveJuggernaut().equals(juggernaut) || juggernaut.getState() != eventState) {
			cancel();
			return;
		}

		onRun();

		ticks++;
	}

	public int getSeconds() {
		return 3 - ticks;
	}

	public abstract void onRun();

}
