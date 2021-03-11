package me.drizzy.practice.event.types.oitc.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OITCPlayer {

    @Getter
    private final UUID uuid;
    @Getter
    private final String username;
    @Getter
    @Setter
    private OITCPlayerState state = OITCPlayerState.WAITING;

    public OITCPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
