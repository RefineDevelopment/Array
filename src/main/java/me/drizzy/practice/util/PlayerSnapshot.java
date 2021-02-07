package me.drizzy.practice.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerSnapshot {

    @Getter
    private final UUID uuid;
    @Getter
    private final String username;

    public PlayerSnapshot(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
