package xyz.refinedev.practice.util.timer.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.util.events.BaseEvent;
import xyz.refinedev.practice.util.timer.Timer;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Getter
public class TimerExtendEvent extends BaseEvent {

	private final Optional<Player> player;
	private final Optional<UUID> userUUID;
	private final Timer timer;
	private final long previousDuration;
	private boolean cancelled;
	private long newDuration;

	public TimerExtendEvent(Timer timer, long previousDuration, long newDuration) {
		this.player = Optional.empty();
		this.userUUID = Optional.empty();
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}

	public TimerExtendEvent(@Nullable Player player, UUID uniqueId, Timer timer, long previousDuration, long newDuration) {
		this.player = Optional.ofNullable(player);
		this.userUUID = Optional.ofNullable(uniqueId);
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}
}
