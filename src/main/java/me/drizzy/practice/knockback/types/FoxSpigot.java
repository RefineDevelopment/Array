package me.drizzy.practice.knockback.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import me.drizzy.practice.util.external.BukkitReflection;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

import java.lang.reflect.Method;

public class FoxSpigot implements KnockbackType {

    public FoxSpigot() {
        Array.logger("&bFound FoxSpigot! Hooking in....");
    }

    @Override
    public void applyKnockback(Player p, String s) {
        KnockbackProfile knockbackProfile=KnockbackModule.INSTANCE.profiles.get(s);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Method getHandle = p.getClass().getMethod("getHandle");
                    Object nms = getHandle.invoke(p);
                    Method setKnockback = nms.getClass().getMethod("setKnockback", KnockbackProfile.class);
                    setKnockback.invoke(nms, knockbackProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Array.getInstance());
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
        KnockbackProfile knockbackProfile;
        if (kit.getKnockbackProfile() != null) {
            knockbackProfile = KnockbackModule.INSTANCE.profiles.get(kit.getKnockbackProfile());
        } else {
            knockbackProfile = KnockbackModule.getDefault();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Method getHandle = p.getClass().getMethod("getHandle");
                    Object nms = getHandle.invoke(p);
                    Method setKnockback = nms.getClass().getMethod("setKnockback", KnockbackProfile.class);
                    setKnockback.invoke(nms, knockbackProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Array.getInstance());
    }

    @Override
    public void applyDefaultKnockback(Player p) {
        KnockbackProfile knockbackProfile = KnockbackModule.getDefault();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Method getHandle = p.getClass().getMethod("getHandle");
                    Object nms = getHandle.invoke(p);
                    Method setKnockback = nms.getClass().getMethod("setKnockback", KnockbackProfile.class);
                    setKnockback.invoke(nms, knockbackProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Array.getInstance());
    }
}
