package xyz.refinedev.practice.util.other;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class PlayerSnapshot {

    private final UUID uuid;
    private final String username;

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
