package me.drizzy.practice.events.types.lms.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class LMSPlayer {

    private final UUID uuid;
    private final String username;
    private LMSPlayerState state = LMSPlayerState.WAITING;

    public LMSPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
