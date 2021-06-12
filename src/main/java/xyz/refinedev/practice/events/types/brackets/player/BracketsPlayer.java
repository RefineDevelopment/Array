package xyz.refinedev.practice.events.types.brackets.player;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class BracketsPlayer {

	private final UUID uuid;
	private final String username;
	private BracketsPlayerState state = BracketsPlayerState.WAITING;
	private int roundWins = 0;

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

	public int getPing() {
		return PlayerUtil.getPing(getPlayer());
	}

}
