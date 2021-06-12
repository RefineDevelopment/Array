package xyz.refinedev.practice.pvpclasses.events;

import xyz.refinedev.practice.pvpclasses.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player equips a {@link PvPClass}.
 */
public class ArmorClassEquipEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final PvPClass pvpClass;

    public ArmorClassEquipEvent(Player player, PvPClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the {@link PvPClass} being unequipped.
     *
     * @return the unequipped {@link PvPClass}
     */
    public PvPClass getPvpClass() {
        return pvpClass;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
