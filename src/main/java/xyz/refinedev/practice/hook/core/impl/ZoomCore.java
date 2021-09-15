package xyz.refinedev.practice.hook.core.impl;

import club.frozed.core.ZoomAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;

public class ZoomCore implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return ZoomAPI.getRankName(player.getPlayer());
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return ZoomAPI.getRankPrefix(player.getPlayer());
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return ZoomAPI.getRankSuffix(player.getPlayer());
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return ZoomAPI.getRankPrefix(player.getPlayer()) + player.getName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ZoomAPI.getRankColor(player.getPlayer());
    }
}
