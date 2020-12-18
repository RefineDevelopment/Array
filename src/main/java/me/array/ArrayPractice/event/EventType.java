package me.array.ArrayPractice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum EventType {
    BRACKETS(Array.get().getBracketsManager().getActiveBrackets(), "Brackets", Material.IRON_SWORD),
    SUMO(Array.get().getSumoManager().getActiveSumo(), "Sumo", Material.LEASH),
    FFA(Array.get().getFfaManager().getActiveFFA(), "FFA", Material.DIAMOND_AXE),
    PARKOUR(Array.get().getParkourManager().getActiveParkour(), "Parkour", Material.DIAMOND_BOOTS),
    SPLEEF(Array.get().getSpleefManager().getActiveSpleef(), "Spleef", Material.DIAMOND_SPADE);

    private final Object object;
    private final String title;
    private final Material material;
}
