package me.drizzy.practice.event.types.brackets.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BracketsPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private BracketsPlayerState state = BracketsPlayerState.WAITING;
	@Getter @Setter private int roundWins = 0;

	public BracketsPlayer(Player player) {
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
