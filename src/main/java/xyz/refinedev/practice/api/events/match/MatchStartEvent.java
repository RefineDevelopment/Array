package xyz.refinedev.practice.api.events.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.events.BaseEvent;

@Getter
@RequiredArgsConstructor
public class MatchStartEvent extends BaseEvent {

    private final Match match;
}
