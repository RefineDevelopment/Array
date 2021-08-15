package me.drizzy.practice.api.events;

import me.drizzy.practice.match.Match;
import lombok.Getter;
import org.bukkit.event.HandlerList;

@Getter
public class MatchEndEvent extends MatchEvent {

    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public MatchEndEvent(final Match match) {
        super(match);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        this.cancelled = b;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
