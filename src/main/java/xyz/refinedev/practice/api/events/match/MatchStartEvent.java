package xyz.refinedev.practice.api.events.match;

import xyz.refinedev.practice.match.Match;
import lombok.Getter;

@Getter
public class MatchStartEvent extends MatchEvent {

    public MatchStartEvent(final Match match) {
        super(match);
    }
}
