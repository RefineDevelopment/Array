package xyz.refinedev.practice.hook.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/16/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class CoreHandler {

    private final Array plugin;
    private CoreType coreType;

    public void init() {
        if (!plugin.getConfigHandler().isCORE_HOOK_ENABLED()) return;
        this.coreType = CoreType.get();

        if (coreType.equals(CoreType.DEFAULT)) {
            plugin.logger("&7No compatible Core was found, defaulting to &aGreen &7color!");
            return;
        }
        plugin.logger("&7Found &c" + coreType.getName() + "&7, Implementing Core Hook...");
    }

    /**
     * Returns the full name of specified player
     * from the core hook
     *
     * @param player {@link Player} whose name we fetching
     * @return {@link String} full name
     */
    public String getFullName(Player player) {
        return coreType.getCoreAdapter().getFullName(player);
    }

    /**
     * Returns the rank name of specified player
     * from the core hook
     *
     * @param player {@link Player} whose rank name we fetching
     * @return {@link String} rank name
     */
    public String getRankName(Player player) {
        return coreType.getCoreAdapter().getRankName(player);
    }

    /**
     * Returns the rank prefix of specified player
     * from the core hook
     *
     * @param player {@link Player} whose rank prefix we fetching
     * @return {@link String} rank prefix
     */
    public String getRankPrefix(Player player) {
        return coreType.getCoreAdapter().getRankPrefix(player);
    }

    /**
     * Returns the rank suffix of specified player
     * from the core hook
     *
     * @param player {@link Player} whose rank suffix we fetching
     * @return {@link String} rank suffix
     */
    public String getRankSuffix(Player player) {
        return coreType.getCoreAdapter().getRankSuffix(player);
    }

    /**
     * Returns the rank color of specified player
     * from the core hook
     *
     * @param player {@link Player} whose color suffix we fetching
     * @return {@link ChatColor} rank color
     */
    public ChatColor getRankColor(Player player) {
        return coreType.getCoreAdapter().getRankColor(player);
    }

}
