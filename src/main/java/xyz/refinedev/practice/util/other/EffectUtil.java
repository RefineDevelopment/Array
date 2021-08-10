package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.Effect;
import org.bukkit.Location;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

@UtilityClass
public class EffectUtil {

    public static void sendEffect(Effect particle, Location location, int id, float offsetX, float offsetZ) {
        location.getWorld().spigot().playEffect(location, particle, id, 0, offsetX, 1.0f, offsetZ, 0.04f, 20, 35);
    }
}
