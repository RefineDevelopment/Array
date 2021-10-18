package xyz.refinedev.practice.profile.hotbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HotbarType {

    QUEUE_JOIN_RANKED,
    QUEUE_JOIN_UNRANKED,
    QUEUE_JOIN_CLAN,
    QUEUE_LEAVE,
    
    PARTY_EVENTS, 
    PARTY_CREATE, 
    PARTY_DISBAND, 
    PARTY_LEAVE,
    PARTY_INFO,
    PARTY_CLASSES,
    PARTY_SETTINGS,
    OTHER_PARTIES,

    KIT_EDITOR,

    SPECTATOR_SHOW,
    SPECTATOR_HIDE,
    SPECTATE_MENU,
    SPECTATE_STOP, 
    
    EVENT_JOIN,
    EVENT_TEAM,
    EVENT_LEAVE,
    
    REMATCH_REQUEST,
    REMATCH_ACCEPT,
    
    PARKOUR_SPAWN,
    SPLEEF_MATCH,
    GULAG_GUN,
    
    DEFAULT_KIT,

    CUSTOM;

}
