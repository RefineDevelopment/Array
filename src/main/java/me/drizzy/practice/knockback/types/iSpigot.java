package me.drizzy.practice.knockback.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import spg.lgdev.knockback.Knockback;

import java.lang.reflect.Method;

public class iSpigot implements KnockbackType {

    public iSpigot() {
        Array.logger("&bFound iSpigot! Hooking in....");
    }

    @Override
    public void applyKnockback(Player p, String s) {
        Knockback knockbackProfile = spg.lgdev.iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile(s);
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockback", Knockback.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
        Knockback knockbackProfile;
        if (kit.getKnockbackProfile() != null) {
            knockbackProfile = spg.lgdev.iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile(kit.getKnockbackProfile());
        } else {
            knockbackProfile = spg.lgdev.iSpigot.INSTANCE.getKnockbackHandler().getDefaultKnockback();
        }
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockback", Knockback.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyDefaultKnockback(Player p) {
        Knockback knockbackProfile = spg.lgdev.iSpigot.INSTANCE.getKnockbackHandler().getDefaultKnockback();
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockback", Knockback.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

