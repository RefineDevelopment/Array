package me.drizzy.practice.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.util.events.BaseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@AllArgsConstructor
public class MatchPlayerSetupEvent extends BaseEvent {

    public final Player player;
    public final Match match;
}
