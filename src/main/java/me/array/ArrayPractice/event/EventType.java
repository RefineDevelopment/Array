package me.array.ArrayPractice.event;

import me.array.ArrayPractice.Practice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum EventType {
    BRACKETS(Practice.get().getBracketsManager().getActiveBrackets(), "Brackets", Material.IRON_SWORD),
    SUMO(Practice.get().getSumoManager().getActiveSumo(), "Sumo", Material.LEASH),
    LMS(Practice.get().getLMSManager().getActiveLMS(), "LMS", Material.DIAMOND_AXE),
    PARKOUR(Practice.get().getParkourManager().getActiveParkour(), "Parkour", Material.DIAMOND_BOOTS),
    SPLEEF(Practice.get().getSpleefManager().getActiveSpleef(), "Spleef", Material.DIAMOND_SPADE),
    SKYWARS(Practice.get().getSkyWarsManager().getActiveSkyWars(), "Skywars", Material.DIAMOND_AXE);

    private final Object object;
    private final String title;
    private final Material material;
}
