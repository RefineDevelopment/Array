package xyz.refinedev.practice.profile.rank.apis;

import club.frozed.core.ZoomAPI;
import xyz.refinedev.practice.profile.rank.RankType;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class ZoomCore implements RankType {
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
    public boolean isBusy(OfflinePlayer player) {
        return ZoomAPI.isStaffChat(player.getPlayer());
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ZoomAPI.getRankColor(player.getPlayer());
    }
}
