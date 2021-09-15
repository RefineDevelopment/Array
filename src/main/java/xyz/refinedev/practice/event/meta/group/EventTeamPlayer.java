package xyz.refinedev.practice.event.meta.group;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.event.meta.player.EventPlayer;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/8/2021
 * Project: Array
 */

@Getter @Setter
public class EventTeamPlayer extends EventPlayer {

    private EventGroup group;

    public EventTeamPlayer(Player player) {
        super(player);
    }
}