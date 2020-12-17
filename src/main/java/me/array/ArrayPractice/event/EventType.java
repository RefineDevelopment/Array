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
    JUGGERNAUT(Array.get().getJuggernautManager().getActiveJuggernaut(), "Juggernaut", Material.CHAINMAIL_CHESTPLATE),
    PARKOUR(Array.get().getParkourManager().getActiveParkour(), "Parkour", Material.DIAMOND_BOOTS),
    WIPEOUT(Array.get().getWipeoutManager().getActiveWipeout(), "Wipeout", Material.WATER_BUCKET),
    SKYWARS(Array.get().getSkyWarsManager().getActiveSkyWars(), "SkyWars", Material.BOW),
    SPLEEF(Array.get().getSpleefManager().getActiveSpleef(), "Spleef", Material.DIAMOND_SPADE),
    INFECTED(Array.get().getInfectedManager().getActiveInfected(), "Infected", Material.SLIME_BALL);

    private Object object;
    private String title;
    private Material material;
}
