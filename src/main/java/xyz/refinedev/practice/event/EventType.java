package xyz.refinedev.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EventType {
    
    BRACKETS("Brackets"),
    SUMO("Sumo"),
    GULAG("Gulag"),
    LMS("LMS"),
    PARKOUR("Parkour"),
    SPLEEF("Spleef"),
    OITC("OITC"),
    KOTH("KoTH"),
    PAINTBALL("Paintball"),
    JUGGERNAUT("Juggernaut");

    private final String name;

    public static EventType getByName(String name) {
        return Arrays.stream(EventType.values()).filter(type -> type.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
