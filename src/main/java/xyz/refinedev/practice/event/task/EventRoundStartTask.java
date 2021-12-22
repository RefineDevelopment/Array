package xyz.refinedev.practice.event.task;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.util.other.PlayerUtil;

public class EventRoundStartTask extends EventTask {

	public EventRoundStartTask(Array plugin, Event event) {
		super(plugin, event, EventState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (this.getEvent().isFreeForAll()) {
			//TODO: lol
			return;
		}

		if (getTicks() >= 3) {
			this.getEvent().broadcastMessage(Locale.EVENT_ROUND_STARTED.toString());
			this.getEvent().setEventTask(null);
			this.getEvent().setState(EventState.ROUND_FIGHTING);

			Player playerA = this.getEvent().getRoundPlayerA().getPlayer();
			Player playerB = this.getEvent().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F); //TODO: Make Configurable
				PlayerUtil.allowMovement(playerA);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F); //TODO: Make Configurable
				PlayerUtil.allowMovement(playerB);
			}

			this.getEvent().setRoundStart(System.currentTimeMillis());
		} else {
			Player playerA = this.getEvent().getRoundPlayerA().getPlayer();
			Player playerB = this.getEvent().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F); //TODO: Make Configurable
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F); //TODO: Make Configurable
			}

			this.getEvent().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(this.getSeconds())));
		}
	}
}
