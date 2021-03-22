package me.drizzy.practice.knockback.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import org.bukkit.entity.Player;

public class Default implements KnockbackType {

    public Default() {
        Array.logger("&bNo Knockback Spigot was found, Knockback Support will NOT work!");
    }

    @Override
    public void applyKnockback(Player p, String s) {
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
    }

    @Override
    public void applyDefaultKnockback(Player p) {
    }
}
