package me.drizzy.practice.events.types.parkour.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class ParkourPlayer {

	private final UUID uuid;
	private final String username;
	private Location lastLocation;
	private ParkourPlayerState state = ParkourPlayerState.WAITING;

	public ParkourPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
