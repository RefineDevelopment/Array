/*package me.drizzy.practice.knockback.types;

import com.minexd.spigot.knockback.KnockbackProfile;
import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SpigotX implements KnockbackType {

    public SpigotX() {
        Array.logger("Found SpigotX! Hooking in...");
    }

    @Override
    public void applyKnockback(Player p, String s) {
        KnockbackProfile knockbackProfile;
        knockbackProfile = com.minexd.spigot.SpigotX.INSTANCE.getConfig().getKbProfileByName(s);
        if (knockbackProfile != null) {
            ((CraftPlayer) p).getHandle().setKnockbackProfile(knockbackProfile);
        } else {
            knockbackProfile=com.minexd.spigot.SpigotX.INSTANCE.getConfig().getCurrentKb();
            ((CraftPlayer) p).getHandle().setKnockbackProfile(knockbackProfile);
        }
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
        KnockbackProfile knockbackProfile;
        knockbackProfile = com.minexd.spigot.SpigotX.INSTANCE.getConfig().getKbProfileByName(kit.getKnockbackProfile());
        if (knockbackProfile != null) {
            ((CraftPlayer) p).getHandle().setKnockbackProfile(knockbackProfile);
        } else {
            knockbackProfile=com.minexd.spigot.SpigotX.INSTANCE.getConfig().getCurrentKb();
            ((CraftPlayer) p).getHandle().setKnockbackProfile(knockbackProfile);
        }
    }

    @Override
    public void applyDefaultKnockback(Player p) {
        KnockbackProfile knockbackProfile = com.minexd.spigot.SpigotX.INSTANCE.getConfig().getCurrentKb();
        ((CraftPlayer) p).getHandle().setKnockbackProfile(knockbackProfile);
    }
}
*/