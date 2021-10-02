package xyz.refinedev.practice.api.events.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.events.BaseEvent;

@Getter @Setter
@RequiredArgsConstructor
public class MatchEvent extends BaseEvent {

    public final Match match;
}
