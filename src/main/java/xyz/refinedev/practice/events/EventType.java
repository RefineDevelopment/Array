package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EventType {
    
    BRACKETS_SOLO("BracketsSolo"),
    BRACKETS_TEAM("BracketsTeam"),
    SUMO_SOLO("SumoSolo"),
    SUMO_TEAM("SumoTeam"),
    GULAG_SOLO("GulagSolo"),
    GULAG_TEAM("GulagTeam"),
    LMS("LMS"),
    PARKOUR("Parkour"),
    SPLEEF("Spleef"),
    OITC("OITC"),
    KOTH("KoTH"),
    PAINTBALL("Paintball"),
    OMA("OMA");

    private final String name;

    public static EventType getByName(String name) {
        return Arrays.stream(EventType.values()).filter(type -> type.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
