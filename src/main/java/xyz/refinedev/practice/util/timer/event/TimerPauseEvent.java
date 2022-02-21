package xyz.refinedev.practice.util.timer.event;

import lombok.Getter;
import xyz.refinedev.practice.util.events.BaseEvent;
import xyz.refinedev.practice.util.timer.Timer;

import java.util.Optional;
import java.util.UUID;

@Getter
public class TimerPauseEvent extends BaseEvent {

	private final boolean paused;
	private final Optional<UUID> userUUID;
	private final Timer timer;

	public TimerPauseEvent(Timer timer, boolean paused) {
		this.userUUID = Optional.empty();
		this.timer = timer;
		this.paused = paused;
	}

	public TimerPauseEvent(UUID userUUID, Timer timer, boolean paused) {
		this.userUUID = Optional.ofNullable(userUUID);
		this.timer = timer;
		this.paused = paused;
	}
}
