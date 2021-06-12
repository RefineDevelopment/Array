package xyz.refinedev.practice.events.types.spleef.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class SpleefPlayer {

	private final UUID uuid;
	private final String username;
	private SpleefPlayerState state = SpleefPlayerState.WAITING;

	public SpleefPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

}
