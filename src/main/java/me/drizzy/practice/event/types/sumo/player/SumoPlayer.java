package me.drizzy.practice.event.types.sumo.player;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SumoPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private SumoPlayerState state = SumoPlayerState.WAITING;
	@Getter @Setter private int roundWins = 0;

	public SumoPlayer(Player player) {
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
