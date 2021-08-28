package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PotionUtil {

    public String getName(PotionEffectType potionEffectType) {
        switch (potionEffectType.getName().toLowerCase()) {
            case "fire_resistance": return "Fire Resistance";
            case "weakness": return "weakness";
            case "speed": return "Speed";
            case "slowness": return "Slowness";
            case "absorption": return "Absorption";
            case "regeneration": return "Regeneration";
            case "damage_resistance": return "Resistance";
            default: return potionEffectType.getName();
        }
    }

}
