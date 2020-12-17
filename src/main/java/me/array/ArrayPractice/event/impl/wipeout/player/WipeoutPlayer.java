package me.array.ArrayPractice.event.impl.wipeout.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WipeoutPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private Location lastLocation;
	@Getter @Setter private WipeoutPlayerState state = WipeoutPlayerState.WAITING;

	public WipeoutPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
