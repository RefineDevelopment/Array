package xyz.refinedev.practice.api.events.match;

import xyz.refinedev.practice.match.Match;
import lombok.Getter;

@Getter
public class MatchEndEvent extends MatchEvent {

    public MatchEndEvent(final Match match) {
        super(match);
    }

}
