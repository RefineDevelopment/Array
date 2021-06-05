package me.drizzy.practice.events.types.sumo.player;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter @Setter
public class SumoPlayer {

	private final UUID uuid;
	private final String username;
	private SumoPlayerState state = SumoPlayerState.WAITING;
	private int roundWins = 0;

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

	public int getPing() {
		return PlayerUtil.getPing(getPlayer());
	}

}
