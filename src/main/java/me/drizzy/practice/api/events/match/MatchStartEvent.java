package me.drizzy.practice.api.events.match;

import me.drizzy.practice.match.Match;
import lombok.Getter;

@Getter
public class MatchStartEvent extends MatchEvent {

    public MatchStartEvent(final Match match) {
        super(match);
    }
}
