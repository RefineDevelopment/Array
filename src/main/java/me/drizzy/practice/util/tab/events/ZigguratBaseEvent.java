package me.drizzy.practice.util.tab.events;

import lombok.*;
import org.bukkit.event.*;

public class ZigguratBaseEvent extends Event implements Cancellable {

    @Getter public static HandlerList handlerList = new HandlerList();
    @Getter @Setter private boolean cancelled = false;

    public ZigguratBaseEvent() {

    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
