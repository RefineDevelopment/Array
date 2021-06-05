package me.drizzy.practice.api.events.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.util.events.BaseEvent;
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
public class MatchSpectatorJoinEvent extends BaseEvent {

    public final Player spectator;
    public final Match match;

}
