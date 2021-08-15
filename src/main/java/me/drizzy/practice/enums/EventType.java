package me.drizzy.practice.enums;

import lombok.Setter;
import me.drizzy.practice.Array;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.drizzy.practice.events.types.gulag.Gulag;
import org.bukkit.Material;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.sumo.Sumo;

@AllArgsConstructor
@Getter
public enum EventType {
    BRACKETS(Array.getInstance().getBracketsManager().getActiveBrackets(), "&c&lBrackets", Material.IRON_SWORD, Brackets.isEnabled(), Brackets.getMaxPlayers()),
    SUMO(Array.getInstance().getSumoManager().getActiveSumo(), "&c&lSumo", Material.LEASH, Sumo.isEnabled(), Sumo.getMaxPlayers()),
    LMS(Array.getInstance().getLMSManager().getActiveLMS(), "&c&lLMS", Material.DIAMOND_SWORD, me.drizzy.practice.events.types.lms.LMS.isEnabled(), me.drizzy.practice.events.types.lms.LMS.getMaxPlayers()),
    PARKOUR(Array.getInstance().getParkourManager().getActiveParkour(), "&c&lParkour", Material.FEATHER, Parkour.isEnabled(), Parkour.getMaxPlayers()),
    GULAG(Array.getInstance().getGulagManager().getActiveGulag(), "&c&lGulag", Material.IRON_FENCE, Gulag.isEnabled(), Gulag.getMaxPlayers()),
    SPLEEF(Array.getInstance().getSpleefManager().getActiveSpleef(), "&c&lSpleef", Material.SNOW_BALL, Spleef.isEnabled(), Spleef.getMaxPlayers()),
    OITC(null, "&c&lOITC", Material.BOW, false, 0),
    KOTH(null, "&c&lKoTH", Material.IRON_BOOTS, false, 0);

    private final Object object;
    private final String title;
    private final Material material;
    @Setter
    private boolean enabled;
    private final int limit;

}
