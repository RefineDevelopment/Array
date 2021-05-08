package me.drizzy.practice.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.util.events.BaseEvent;
import org.bukkit.entity.Player;

/**
 * @author Drizzy
 * Created at 4/19/2021
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchSpectatorLeaveEvent extends BaseEvent {

    public final Player spectator;
    public final Match match;

}
