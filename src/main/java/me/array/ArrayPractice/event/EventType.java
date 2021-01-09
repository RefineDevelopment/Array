package me.array.ArrayPractice.event;

import me.array.ArrayPractice.Practice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum EventType {
    BRACKETS(Practice.getInstance().getBracketsManager().getActiveBrackets(), "Brackets", Material.IRON_SWORD),
    SUMO(Practice.getInstance().getSumoManager().getActiveSumo(), "Sumo", Material.LEASH),
    LMS(Practice.getInstance().getLMSManager().getActiveLMS(), "LMS", Material.DIAMOND_AXE),
    PARKOUR(Practice.getInstance().getParkourManager().getActiveParkour(), "Parkour", Material.DIAMOND_BOOTS),
    SPLEEF(Practice.getInstance().getSpleefManager().getActiveSpleef(), "Spleef", Material.DIAMOND_SPADE),
    SKYWARS(Practice.getInstance().getSkyWarsManager().getActiveSkyWars(), "Skywars", Material.DIAMOND_AXE);

    private final Object object;
    private final String title;
    private final Material material;
}
