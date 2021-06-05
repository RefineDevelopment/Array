package me.drizzy.practice.api.events.match;

import me.drizzy.practice.match.Match;
import lombok.Getter;

@Getter
public class MatchEndEvent extends MatchEvent {

    public MatchEndEvent(final Match match) {
        super(match);
    }

}
