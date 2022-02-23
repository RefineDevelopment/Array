package xyz.refinedev.practice.profile.killeffect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.refinedev.practice.util.location.LightningUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.Arrays;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/23/2022
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public enum KillEffect {

    NONE(Material.AIR, "none", "&7None", null, (player, watchers, drops) -> {
    }),
    SPLASH(Material.WATER_BUCKET, "splash", "&9Splash", "array.effect.splash", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
    }),
    FLAME(Material.FLINT_AND_STEEL, "flame", "&eFlame","array.effect.flame", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
    }),
    EXPLOSION(Material.TNT, "explosion", "&cExplosion","array.effect.explosion", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
    }),
    ENDER(Material.ENDER_PEARL, "ender", "&aEnder", "array.effect.ender", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
    }),
    LIGHTNING(Material.BLAZE_ROD, "lightning", "&bLightning","array.effect.firework", (player, watchers, drops) -> {
        watchers.forEach(watcher -> LightningUtil.spawnLightning(watcher, player.getLocation()));
        PlayerUtil.animateDeath(player);
    }),
    FIREWORK(Material.FIREWORK, "firework", "&bFirework Spark","array.effect.firework", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
    });

    private final Material icon;
    private final String name, displayName, permission;
    private final EffectCallable callable;

    /**
     * Does the player have the permission to play this effect
     * 
     * @param player {@link Player player} the player triggering the effect
     * @return       {@link Boolean}
     */
    public boolean hasPermission(Player player) {
        return permission == null || player.hasPermission(permission);
    }

    public static KillEffect getByName(String input) {
        return Arrays.stream(KillEffect.values()).filter(type -> type.name().equalsIgnoreCase(input) || type.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
    }
}
