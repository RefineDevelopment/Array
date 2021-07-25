package xyz.refinedev.practice.api.events.match;

import lombok.Getter;
import xyz.refinedev.practice.match.Match;

@Getter
public class MatchStartEvent extends MatchEvent {

    public MatchStartEvent(final Match match) {
        super(match);
    }
}
