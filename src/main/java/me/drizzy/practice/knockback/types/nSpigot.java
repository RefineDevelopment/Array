package me.drizzy.practice.knockback.types;

import com.ngxdev.knockback.KnockbackModule;
import com.ngxdev.knockback.KnockbackProfile;
import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class nSpigot implements KnockbackType {

    public nSpigot() {
        Array.logger("&bFound nSpigot! Hooking in....");
    }

    @Override
    public void applyKnockback(Player p, String s) {
        KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles.get(s);
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockback", KnockbackProfile.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
        KnockbackProfile knockbackProfile;
        if (kit.getKnockbackProfile() != null) {
            knockbackProfile = KnockbackModule.INSTANCE.profiles.get(kit.getKnockbackProfile());
        } else {
            knockbackProfile = KnockbackModule.getDefault();
        }
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockback", KnockbackProfile.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyDefaultKnockback(Player p) {
        KnockbackProfile knockbackProfile = KnockbackModule.getDefault();
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockback", KnockbackProfile.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
