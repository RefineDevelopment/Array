package me.drizzy.practice.knockback.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import org.bukkit.entity.Player;

/**
 * @author Drizzy
 * Created at 4/11/2021
 */
public class Default implements KnockbackType {

    public Default() {
        Array.logger("&cNo Compatible spigot found, Knockback Implementation won't work!");
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
