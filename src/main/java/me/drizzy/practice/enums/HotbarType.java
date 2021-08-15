package me.drizzy.practice.enums;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum HotbarType {

    QUEUE_JOIN_RANKED(null), 
    QUEUE_JOIN_UNRANKED(null), 
    QUEUE_LEAVE(null),
    PARTY_EVENTS(null), 
    PARTY_CREATE("party create"), 
    PARTY_DISBAND("party disband"), 
    PARTY_LEAVE("party leave"),
    PARTY_INFORMATION("party info"),
    PARTY_SETTINGS(null), 
    OTHER_PARTIES(null), 
    PARTY_INFO(null),
    KIT_EDITOR(null),
    MAIN_MENU(null),
    SPECTATE_STOP("stopspectating"), 
    EVENT_JOIN("events"),
    SUMO_LEAVE("sumo leave"), 
    BRACKETS_LEAVE("brackets leave"), 
    LMS_LEAVE("lms leave"),
    PARKOUR_LEAVE("parkour leave"),
    PARKOUR_SPAWN(null),
    SPLEEF_LEAVE("spleef leave"),
    REMATCH_REQUEST("rematch"), 
    REMATCH_ACCEPT("rematch"),
    SPLEEF_MATCH(null),
    GULAG_GUN(null),
    GULAG_LEAVE("gulag leave"),
    OITC_LEAVE("oitc leave"),
    OITC_KIT(null),
    DEFAULT_KIT(null), 
    DIAMOND_KIT(null), 
    BARD_KIT(null), 
    ROGUE_KIT(null), 
    ARCHER_KIT(null);
    
    private final String command;
    
    public String getCommand() {
        return this.command;
    }
}
