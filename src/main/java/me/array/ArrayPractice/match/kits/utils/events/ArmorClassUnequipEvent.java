package me.array.ArrayPractice.match.kits.utils.events;

import me.array.ArrayPractice.match.kits.utils.ArmorClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player unequips a {@link ArmorClass}.
 */
public class ArmorClassUnequipEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ArmorClass pvpClass;

    public ArmorClassUnequipEvent(Player player, ArmorClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the {@link ArmorClass} being unequipped.
     *
     * @return the unequipped {@link ArmorClass}
     */
    public ArmorClass getPvpClass() {
        return pvpClass;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
