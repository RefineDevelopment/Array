package me.drizzy.practice.event.types.spleef.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpleefPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private SpleefPlayerState state = SpleefPlayerState.WAITING;

	public SpleefPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
