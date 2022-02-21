package xyz.refinedev.practice.util.timer;

import lombok.Data;
import lombok.Getter;
import org.bukkit.event.Listener;
import xyz.refinedev.practice.Array;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TimerHandler implements Listener {

	@Getter
	private final Set<Timer> timers = new LinkedHashSet<>();
	private final Array plugin;

	public void registerTimer(Timer timer) {
		this.timers.add(timer);
		if (timer instanceof Listener) {
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
		}
	}

	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	public <T extends Timer> T getTimer(Class<T> timerClass) {
		for (Timer timer : this.timers) {
			if (timer.getClass().equals(timerClass)) {
				return (T) timer;
			}
		}

		return null;
	}
}
