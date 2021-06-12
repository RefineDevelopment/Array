package xyz.refinedev.practice.events.types.brackets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public abstract class BracketsTask extends BukkitRunnable {

	private int ticks;
	private final Brackets brackets;
	private final BracketsState eventState;

	@Override
	public void run() {
		if (Array.getInstance().getBracketsManager().getActiveBrackets() == null ||
		    !Array.getInstance().getBracketsManager().getActiveBrackets().equals(brackets) || brackets.getState() != eventState) {
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
