package me.drizzy.practice.api.events.match;

import lombok.AllArgsConstructor;
import lombok.Setter;
import me.drizzy.practice.match.Match;
import lombok.Getter;
import me.drizzy.practice.util.events.BaseEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@AllArgsConstructor
public class MatchEvent extends BaseEvent {

    public final Match match;
}
