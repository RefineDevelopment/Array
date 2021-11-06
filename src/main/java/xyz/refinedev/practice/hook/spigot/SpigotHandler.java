package xyz.refinedev.practice.hook.spigot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.other.TaskUtil;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/27/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class SpigotHandler {

    private final Array plugin;
    private SpigotType spigotType;

    public void init() {
        this.spigotType = SpigotType.get();

        if (spigotType.equals(SpigotType.Default)) {
            TaskUtil.runLater(() -> plugin.getConfigHandler().setHCF_ENABLED(false), 10L);
            plugin.logger("&7No compatible Spigot was found, Knockback implementation and HCF will not work!");
            return;
        }
        plugin.getServer().getPluginManager().registerEvents(this.spigotType.getListener(), plugin);
        plugin.logger("&7Found &c" + spigotType.getName() + "&7, Implementing Knockback...");
    }

    /**
     * Apply a knockback profile to the specified player
     *
     * @param player The player who we are applying kb to
     * @param name The name of the Knockback Profile
     */
    public void knockback(Player player, String name) {
        spigotType.getKnockbackType().applyKnockback(player, name);
    }

    /**
     * Apply a kit's knockback profile to the specified player
     *
     * @param player The player who we are applying kb to
     * @param kit The kit whose knockback profile we are getting
     */
    public void kitKnockback(Player player, Kit kit) {
        spigotType.getKnockbackType().appleKitKnockback(player, kit);
    }

    /**
     * Reset a player's knockback profile to default
     *
     * @param player The player we are resetting
     */
    public void resetKnockback(Player player) {
        spigotType.getKnockbackType().applyDefaultKnockback(player);
    }
}
