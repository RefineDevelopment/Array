package xyz.refinedev.practice.event.task;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.util.other.PlayerUtil;

public class EventRoundStartTask extends EventTask {
	
	private final Event event;

	public EventRoundStartTask(Array plugin, Event event) {
		super(plugin, event, EventState.ROUND_STARTING);
		
		this.event = event;
	}

	@Override
	public void onRun() {
		if (this.event.isFreeForAll()) {
			if (getTicks() >= 3) {
				this.event.broadcastMessage(Locale.EVENT_STARTED.toString());
				this.event.setEventTask(null);
				this.event.setState(EventState.ROUND_FIGHTING);
				this.event.setRoundStart(System.currentTimeMillis());
				if (this.event.getType().equals(EventType.PARKOUR)) this.event.getPlayers().forEach(PlayerUtil::allowMovement);
			} else {
				this.event.broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(this.getSeconds())));
			}
			return;
		}

		if (getTicks() >= 3) {
			this.event.broadcastMessage(Locale.EVENT_ROUND_STARTED.toString());
			this.event.setEventTask(null);
			this.event.setState(EventState.ROUND_FIGHTING);

			Player playerA = this.event.getRoundPlayerA().getPlayer();
			Player playerB = this.event.getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F); //TODO: Make Configurable
				PlayerUtil.allowMovement(playerA);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F); //TODO: Make Configurable
				PlayerUtil.allowMovement(playerB);
			}

			this.event.setRoundStart(System.currentTimeMillis());
		} else {
			Player playerA = this.event.getRoundPlayerA().getPlayer();
			Player playerB = this.event.getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F); //TODO: Make Configurable
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F); //TODO: Make Configurable
			}

			this.event.broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(this.getSeconds())));
		}
	}
}
