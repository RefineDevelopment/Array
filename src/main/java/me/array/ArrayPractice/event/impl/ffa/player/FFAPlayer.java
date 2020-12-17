package me.array.ArrayPractice.event.impl.ffa.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FFAPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private FFAPlayerState state = FFAPlayerState.WAITING;

	public FFAPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
