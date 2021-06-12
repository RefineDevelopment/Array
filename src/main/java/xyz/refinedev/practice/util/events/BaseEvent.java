package xyz.refinedev.practice.util.events;

import xyz.refinedev.practice.Array;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public void call() {
        Array.getInstance().getServer().getPluginManager().callEvent(this);
    }

}
