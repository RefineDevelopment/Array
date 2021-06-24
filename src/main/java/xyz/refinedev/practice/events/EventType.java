package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    
    BRACKETS_SOLO("Brackets"),
    SUMO_SOLO("Sumo"),
    BRACKETS_TEAM("Brackets"),
    SUMO_TEAM("Sumo"),
    GULAG_SOLO("Gulag"),
    GULAG_TEAM("Gulag"),
    LMS("LMS"),
    PARKOUR("Parkour"),
    SPLEEF("Spleef"),
    OITC("OITC"),
    //KOTH("KoTH"),
    //PAINTBALL,
    OMA("OMA");

    private final String readable;
}
