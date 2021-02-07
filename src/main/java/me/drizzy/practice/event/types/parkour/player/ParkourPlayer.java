package me.drizzy.practice.event.types.parkour.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ParkourPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private Location lastLocation;
	@Getter @Setter private ParkourPlayerState state = ParkourPlayerState.WAITING;

	public ParkourPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
