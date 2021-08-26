package xyz.refinedev.practice.util.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.refinedev.practice.Array;

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
