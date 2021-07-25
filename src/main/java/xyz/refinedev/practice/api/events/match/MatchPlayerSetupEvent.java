package xyz.refinedev.practice.api.events.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.events.BaseEvent;

@Getter
@Setter
@AllArgsConstructor
public class MatchPlayerSetupEvent extends BaseEvent {

    public final Player player;
    public final Match match;
}
