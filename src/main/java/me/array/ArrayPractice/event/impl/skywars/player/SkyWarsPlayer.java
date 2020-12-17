package me.array.ArrayPractice.event.impl.skywars.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkyWarsPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private SkyWarsPlayerState state = SkyWarsPlayerState.WAITING;

	public SkyWarsPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
