package xyz.refinedev.practice.util.timer.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.util.events.BaseEvent;
import xyz.refinedev.practice.util.timer.Timer;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
public class TimerExpireEvent extends BaseEvent {

	private final Optional<UUID> userUUID;
	private final Timer timer;

	private Optional<Player> player;

	public TimerExpireEvent(Timer timer) {
		this.userUUID = Optional.empty();
		this.timer = timer;
	}

	public TimerExpireEvent(UUID userUUID, Timer timer) {
		this.userUUID = Optional.ofNullable(userUUID);
		this.timer = timer;
	}

	public TimerExpireEvent(Player player, Timer timer) {
		Objects.requireNonNull(player);

		this.player = Optional.of(player);
		this.userUUID = Optional.of(player.getUniqueId());
		this.timer = timer;
	}
}
