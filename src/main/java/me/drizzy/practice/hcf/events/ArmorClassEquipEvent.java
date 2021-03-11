package me.drizzy.practice.hcf.events;

import me.drizzy.practice.hcf.HCFClasses;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player equips a {@link HCFClasses}.
 */
public class ArmorClassEquipEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final HCFClasses pvpClass;

    public ArmorClassEquipEvent(Player player, HCFClasses pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the {@link HCFClasses} being unequipped.
     *
     * @return the unequipped {@link HCFClasses}
     */
    public HCFClasses getPvpClass() {
        return pvpClass;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
