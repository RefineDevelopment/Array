package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EventType {
    
    BRACKETS_SOLO("BracketsSolo", "Brackets"),
    BRACKETS_TEAM("BracketsTeam", "Brackets"),
    SUMO_SOLO("SumoSolo", "Sumo"),
    SUMO_TEAM("SumoTeam", "Sumo"),
    GULAG_SOLO("GulagSolo", "Gulag"),
    GULAG_TEAM("GulagTeam", "Gulag"),
    LMS("LMS", "LMS"),
    PARKOUR("Parkour", "Parkour"),
    SPLEEF("Spleef", "Spleef"),
    OITC("OITC", "OITC"),
    KOTH("KoTH", "KoTH"),
    PAINTBALL("Paintball", "Paintball"),
    JUGGERNAUT("Juggernaut", "Juggernaut");

    private final String name, eventName;

    public static EventType getByName(String name) {
        return Arrays.stream(EventType.values()).filter(type -> type.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
