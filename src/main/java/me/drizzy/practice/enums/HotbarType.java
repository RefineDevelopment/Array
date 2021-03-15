package me.drizzy.practice.enums;

import java.beans.ConstructorProperties;

public enum HotbarType
{
    QUEUE_JOIN_RANKED(null), 
    QUEUE_JOIN_UNRANKED(null), 
    QUEUE_JOIN_KITPVP(null),
    QUEUE_LEAVE(null), 
    PARTY_EVENTS(null), 
    PARTY_CREATE("party create"), 
    PARTY_DISBAND("party disband"), 
    PARTY_LEAVE("party leave"), 
    PARTY_INFORMATION("party info"), 
    PARTY_SETTINGS(null), 
    OTHER_PARTIES(null), 
    PARTY_INFO(null),
    LEADERBOARDS_MENU(null),
    SETTINGS_MENU(null),
    KIT_EDITOR(null), 
    SPECTATE_STOP("stopspectating"), 
    VIEW_INVENTORY(null), 
    EVENT_JOIN("event"), 
    SUMO_LEAVE("sumo leave"), 
    BRACKETS_LEAVE("brackets leave"), 
    LMS_LEAVE("lms leave"),
    PARKOUR_LEAVE("parkour leave"),
    PARKOUR_SPAWN(null),
    SPLEEF_LEAVE("spleef leave"),
    REMATCH_REQUEST("rematch"), 
    REMATCH_ACCEPT("rematch"),
    WIZARD_WAND(null),
    WIZARD_LEAVE("wizard leave"),
    OITC_LEAVE("oitc leave"),
    OITC_KIT(null),
    DEFAULT_KIT(null), 
    DIAMOND_KIT(null), 
    BARD_KIT(null), 
    ROGUE_KIT(null), 
    ARCHER_KIT(null);
    
    private String command;
    
    @ConstructorProperties({ "command" })
    private HotbarType(final String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return this.command;
    }
}
