package me.array.ArrayPractice.util.external;

import me.array.ArrayPractice.Array;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void call() {
        Array.get().getServer().getPluginManager().callEvent(this);
    }

}
