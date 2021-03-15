package me.drizzy.practice.knockback.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import me.drizzy.ravespigot.knockback.KnockbackModule;
import me.drizzy.ravespigot.knockback.KnockbackProfile;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Rave implements KnockbackType {

    public Rave() {
        Array.logger("&bFound Rave! Hooking in....");
    }

    @Override
    public void applyKnockback(Player p, String s) {
        KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles.get(s);
        ((CraftPlayer)p).getHandle().setKnockback(knockbackProfile);
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
        KnockbackProfile knockbackProfile;
        if (kit.getKnockbackProfile() != null) {
            knockbackProfile = KnockbackModule.INSTANCE.profiles.get(kit.getKnockbackProfile());
        } else {
            knockbackProfile = KnockbackModule.getDefault();
        }
        ((CraftPlayer) p).getHandle().setKnockback(knockbackProfile);
    }

    @Override
    public void applyDefaultKnockback(Player p) {
        KnockbackProfile knockbackProfile = KnockbackModule.getDefault();
        ((CraftPlayer) p).getHandle().setKnockback(knockbackProfile);
    }
}
