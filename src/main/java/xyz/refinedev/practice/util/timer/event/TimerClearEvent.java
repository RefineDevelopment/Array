package xyz.refinedev.practice.util.timer.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.util.events.BaseEvent;
import xyz.refinedev.practice.util.timer.Timer;

@Getter
public class TimerClearEvent extends BaseEvent {

	private final Optional<UUID> userUUID;
	private final Timer timer;
	private Optional<Player> player;

	public TimerClearEvent(Timer timer) {
		this.userUUID = Optional.empty();
		this.timer = timer;
	}

	public TimerClearEvent(UUID userUUID, Timer timer) {
		this.userUUID = Optional.of(userUUID);
		this.timer = timer;
	}

	public TimerClearEvent(Player player, Timer timer) {
		Objects.requireNonNull(player);

		this.player = Optional.of(player);
		this.userUUID = Optional.of(player.getUniqueId());
		this.timer = timer;
	}
}
