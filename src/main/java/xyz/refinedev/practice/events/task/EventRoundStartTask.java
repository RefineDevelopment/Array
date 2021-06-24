package xyz.refinedev.practice.events.task;

import me.joeleoli.nucleus.util.PlayerUtil;
import me.joeleoli.nucleus.util.Style;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.meta.EventTask;
import xyz.refinedev.practice.events.impl.SumoEvent;
import org.bukkit.entity.Player;

public class EventRoundStartTask extends EventTask {

	public EventRoundStartTask(Event event) {
		super(event, EventState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (this.getTicks() >= 3) {
			this.getEvent().setEventTask(null);
			this.getEvent().setState(EventState.ROUND_FIGHTING);

			final Player playerA = this.getEvent().getRoundPlayerA().toPlayer();
			final Player playerB = this.getEvent().getRoundPlayerB().toPlayer();

			PlayerUtil.allowMovement(playerA);
			PlayerUtil.allowMovement(playerB);

			((SumoEvent) this.getEvent()).setRoundStart(System.currentTimeMillis());
		} else {
			final int seconds = this.getSeconds();

			this.getEvent().broadcastMessage(
					Style.YELLOW + "The round will start in " + Style.PINK + (seconds) + " second" +
					(seconds == 1 ? "" : "s") + Style.YELLOW + "...");
		}
	}

}
