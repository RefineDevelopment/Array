package xyz.refinedev.practice.api.events.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import xyz.refinedev.practice.util.events.BaseEvent;


@Getter @Setter
@RequiredArgsConstructor
public class SpawnTeleportEvent extends BaseEvent implements Cancellable {

    private final Player player;
    private final Location location;
    private boolean cancelled;

}
