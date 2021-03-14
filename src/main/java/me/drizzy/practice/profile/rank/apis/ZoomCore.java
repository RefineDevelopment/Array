package me.drizzy.practice.profile.rank.apis;

import club.frozed.core.ZoomAPI;
import me.drizzy.practice.profile.rank.RankType;
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
}
