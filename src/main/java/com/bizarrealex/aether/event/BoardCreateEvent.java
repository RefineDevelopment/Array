package com.bizarrealex.aether.event;

import com.bizarrealex.aether.scoreboard.Board;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final Board board;
    @Getter private final Player player;

    public BoardCreateEvent(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
