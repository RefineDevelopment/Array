package xyz.refinedev.practice.profile.killeffect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.refinedev.practice.util.location.LightningUtil;
import xyz.refinedev.practice.util.other.ParticleEffect;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.Arrays;
import java.util.stream.IntStream;

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
        drops.forEach(Item::remove);
    }),
    SPLASH(Material.WATER_BUCKET, "Splash", "&9Splash", "array.effect.splash", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
        IntStream.range(0, 2).forEach(i -> {
            ParticleEffect.WATER_SPLASH.display(new ParticleEffect.ItemData(Material.WATER, (byte) 0), 0.2f, 0.2f, 0.2f, 0.1f, 5, player.getLocation(), 20.0);
        });
    }),
    BLOOD(Material.REDSTONE, "Blood", "&cBlood", "array.effect.blood", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
        IntStream.range(0, 6).forEach(i -> {
            ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.REDSTONE, (byte) 0), 0.2f, 0.2f, 0.2f, 0.1f, 5, player.getLocation(), 20.0);
            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.2f, 0.2f, 0.2f, 0.1f, 5, player.getLocation(), 20.0);
        });
    }),
    FLAME(Material.FLINT_AND_STEEL, "Flame", "&eFlame","array.effect.flame", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, false, (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 20);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        for (Player pWatcher : watchers) {
            if (pWatcher == player) continue;
            ((CraftPlayer)pWatcher).getHandle().playerConnection.sendPacket(packet);
        }
    }),
    EXPLOSION(Material.TNT, "Explosion", "&cExplosion","array.effect.explosion", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
        ParticleEffect.EXPLOSION_LARGE.display(0.5f, 0.5f, 0.5f, 1.0f, 12, player.getLocation(), 20.0);
    }),
    LIGHTNING(Material.BLAZE_ROD, "Lightning", "&bLightning","array.effect.lightning", (player, watchers, drops) -> {
        watchers.forEach(watcher -> LightningUtil.spawnLightning(watcher, player.getLocation()));
        PlayerUtil.animateDeath(player);
    }),
    HEART(Material.REDSTONE_LAMP_ON, "Hearts", "&4Hearts", "array.effect.hearts", (player, watchers, drops) -> {
        drops.forEach(Item::remove);
        ParticleEffect.HEART.display(0.4f, 0.4f, 0.4f, 0.1f, 10, player.getLocation(), 20.0);
    }),
    FIREWORK(Material.FIREWORK, "Firework", "&bFirework Spark","array.effect.firework", (player, watchers, drops) -> {
        Location location = player.getLocation();
        World world = location.getWorld();
        drops.forEach(Item::remove);
        IntStream.range(0, 3).forEach(i -> world.spawnEntity(location, EntityType.FIREWORK));
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
