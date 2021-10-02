package xyz.refinedev.practice.api.events.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.events.BaseEvent;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/2/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class MatchAfterDeathEvent extends BaseEvent {

    private final Match match;
    private final UUID killer;
    private final UUID perished;
}
