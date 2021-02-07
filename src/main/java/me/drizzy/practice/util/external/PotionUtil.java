package me.drizzy.practice.util.external;

import org.bukkit.potion.PotionEffectType;

public class PotionUtil {

    public static String getName(PotionEffectType potionEffectType) {
        if (potionEffectType.getName().equalsIgnoreCase("fire_resistance")) {
            return "Fire Resistance";
        } else if (potionEffectType.getName().equalsIgnoreCase("speed")) {
            return "Speed";
        } else if (potionEffectType.getName().equalsIgnoreCase("weakness")) {
            return "Weakness";
        } else if (potionEffectType.getName().equalsIgnoreCase("slowness")) {
            return "Slowness";
        } else {
            return "Unknown";
        }
    }

}
