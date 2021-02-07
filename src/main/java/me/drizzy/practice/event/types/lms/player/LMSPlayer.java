package me.drizzy.practice.event.types.lms.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LMSPlayer {

    @Getter
    private final UUID uuid;
    @Getter
    private final String username;
    @Getter
    @Setter
    private LMSPlayerState state = LMSPlayerState.WAITING;

    public LMSPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
