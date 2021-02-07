package me.drizzy.practice.event;

import lombok.Setter;
import me.drizzy.practice.Array;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.sumo.Sumo;

@AllArgsConstructor
@Getter
public enum EventType {
    BRACKETS(Array.getInstance().getBracketsManager().getActiveBrackets(), "&b&lBrackets", Material.IRON_SWORD, Brackets.isEnabled(), Brackets.getMaxPlayers()),
    SUMO(Array.getInstance().getSumoManager().getActiveSumo(), "&b&lSumo", Material.LEASH, Sumo.isEnabled(), Sumo.getMaxPlayers()),
    LMS(Array.getInstance().getLMSManager().getActiveLMS(), "&b&lLMS", Material.DIAMOND_SWORD, me.drizzy.practice.event.types.lms.LMS.isEnabled(), me.drizzy.practice.event.types.lms.LMS.getMaxPlayers()),
    PARKOUR(Array.getInstance().getParkourManager().getActiveParkour(), "&b&lParkour", Material.FEATHER, Parkour.isEnabled(), Parkour.getMaxPlayers()),
    SPLEEF(Array.getInstance().getSpleefManager().getActiveSpleef(), "&b&lSpleef", Material.SNOW_BALL, Spleef.isEnabled(), Spleef.getMaxPlayers()),
    SKYWARS(Array.getInstance().getSkyWarsManager().getActiveSkyWars(), "&b&lSkywars", Material.DIAMOND_AXE, SkyWars.isEnabled(), SkyWars.getMaxPlayers()),
    OITC(null, "&c&lOITC", Material.BOW, false, 0),
    RUNNER(null, "&c&lRunner", Material.IRON_BOOTS, false, 0),
    KOTH(null, "&c&lKoTH", Material.REDSTONE_COMPARATOR, false, 0);

    private final Object object;
    private final String title;
    private final Material material;
    @Setter
    private boolean enabled;
    private final int limit;

}
