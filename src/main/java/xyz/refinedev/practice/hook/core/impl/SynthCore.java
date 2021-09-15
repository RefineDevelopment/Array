package xyz.refinedev.practice.hook.core.impl;

import club.vaxel.core.SynthAPI;
import club.vaxel.core.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

public class SynthCore implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        PlayerData data = SynthAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? "" : data.getHighestRank().getName();
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        PlayerData data = SynthAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? "" : data.getHighestRank().getPrefix();
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        PlayerData data = SynthAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? "" : data.getHighestRank().getSuffix();
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return player.getPlayer().getDisplayName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        PlayerData data = SynthAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? ChatColor.GREEN : data.getHighestRank().getColor();
    }
}
