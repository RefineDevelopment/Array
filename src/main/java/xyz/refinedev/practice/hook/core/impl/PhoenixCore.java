package xyz.refinedev.practice.hook.core.impl;

import dev.phoenix.phoenix.PhoenixAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;
import xyz.refinedev.practice.util.chat.ColourUtils;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

public class PhoenixCore implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return PhoenixAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getName();
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return PhoenixAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getPrefix();
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return PhoenixAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getSuffix();
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return PhoenixAPI.INSTANCE.getProfile(player.getUniqueId()).getNameWithColor();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ColourUtils.format(PhoenixAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getColor());
    }
}
