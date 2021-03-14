package me.drizzy.practice.event.types.wizard.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WizardPlayer {

	@Getter private final UUID uuid;
	@Getter private final String username;
	@Getter @Setter private WizardPlayerState state = WizardPlayerState.WAITING;
	@Getter @Setter private int roundWins = 0;

	public WizardPlayer(Player player) {
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
