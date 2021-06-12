package xyz.refinedev.practice.api.events.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.events.BaseEvent;
import org.bukkit.entity.Player;

@Getter
@Setter
@AllArgsConstructor
public class MatchPlayerSetupEvent extends BaseEvent {

    public final Player player;
    public final Match match;
}
