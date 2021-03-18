package me.drizzy.practice.event.types.gulag.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GulagPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private GulagPlayerState state = GulagPlayerState.WAITING;
	@Getter @Setter private int roundWins = 0;

	public GulagPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public void incrementRoundWins() {
		this.roundWins++;
	}

}
