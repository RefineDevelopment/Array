package xyz.refinedev.practice.api.events.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.events.BaseEvent;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/19/2021
 * Project: Array
 */

@Getter
@Setter
@AllArgsConstructor
public class MatchSpectatorLeaveEvent extends BaseEvent {

    public final Player spectator;
    public final Match match;

}
