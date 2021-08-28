package xyz.refinedev.practice.util.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class PlayerSnapshot {

    private final UUID uuid;
    private final String username;

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
