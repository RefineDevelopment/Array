package me.drizzy.practice.knockback;

import me.drizzy.practice.kit.Kit;
import org.bukkit.entity.Player;

/**
 * @author Drizzy
 * Created at 4/11/2021
 */
public interface KnockbackType {

    void applyKnockback(Player p, String s);

    void appleKitKnockback(Player p, Kit kit);

    void applyDefaultKnockback(Player p);

}
