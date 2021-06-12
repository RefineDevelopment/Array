package xyz.refinedev.practice.api.events.match;

import lombok.AllArgsConstructor;
import lombok.Setter;
import xyz.refinedev.practice.match.Match;
import lombok.Getter;
import xyz.refinedev.practice.util.events.BaseEvent;

@Getter
@Setter
@AllArgsConstructor
public class MatchEvent extends BaseEvent {

    public final Match match;
}
