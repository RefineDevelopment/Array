

package me.array.ArrayPractice.profile.hotbar;

import java.beans.ConstructorProperties;

public enum HotbarItem
{
    QUEUE_JOIN_RANKED((String)null), 
    QUEUE_JOIN_UNRANKED((String)null), 
    QUEUE_JOIN_KITPVP((String)null),
    QUEUE_LEAVE((String)null), 
    PARTY_EVENTS((String)null), 
    PARTY_CREATE("party create"), 
    PARTY_DISBAND("party disband"), 
    PARTY_LEAVE("party leave"), 
    PARTY_INFORMATION("party info"), 
    PARTY_SETTINGS((String)null), 
    OTHER_PARTIES((String)null), 
    PARTY_INFO((String)null),
    LEADERBOARDS_MENU((String)null),
    SETTINGS_MENU((String)null),
    KIT_EDITOR((String)null), 
    SPECTATE_STOP("stopspectating"), 
    VIEW_INVENTORY((String)null), 
    EVENT_JOIN("event"), 
    SUMO_LEAVE("sumo leave"), 
    BRACKETS_LEAVE("brackets leave"), 
    FFA_LEAVE("ffa leave"),
    PARKOUR_LEAVE("parkour leave"),
    SKYWARS_LEAVE("skywars leave"), 
    SPLEEF_LEAVE("spleef leave"),
    REMATCH_REQUEST("rematch"), 
    REMATCH_ACCEPT("rematch"), 
    DEFAULT_KIT((String)null), 
    DIAMOND_KIT((String)null), 
    BARD_KIT((String)null), 
    ROGUE_KIT((String)null), 
    ARCHER_KIT((String)null);
    
    private String command;
    
    @ConstructorProperties({ "command" })
    private HotbarItem(final String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return this.command;
    }
}
