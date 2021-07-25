package xyz.refinedev.practice.api.events.match;

import lombok.Getter;
import xyz.refinedev.practice.match.Match;

@Getter
public class MatchEndEvent extends MatchEvent {

    public MatchEndEvent(final Match match) {
        super(match);
    }

}
