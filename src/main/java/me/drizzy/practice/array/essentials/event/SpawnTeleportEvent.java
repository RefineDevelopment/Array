package me.drizzy.practice.array.essentials.event;

import me.drizzy.practice.util.external.BaseEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class SpawnTeleportEvent extends BaseEvent implements Cancellable {

    @Getter
    private final Player player;
    @Getter
    @Setter
    private Location location;
    @Getter
    @Setter
    private boolean cancelled;

    public SpawnTeleportEvent(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

}
