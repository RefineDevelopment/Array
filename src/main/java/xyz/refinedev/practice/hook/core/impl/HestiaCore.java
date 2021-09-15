package xyz.refinedev.practice.hook.core.impl;

import me.quartz.hestia.HestiaAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;

public class HestiaCore implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return HestiaAPI.instance.getRank(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return HestiaAPI.instance.getRankPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return HestiaAPI.instance.getRankSuffix(player.getUniqueId());
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return HestiaAPI.instance.getRankPrefix(player.getUniqueId()) + player.getName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return HestiaAPI.instance.getRankColor(player.getUniqueId());
    }
}
