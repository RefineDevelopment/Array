package xyz.refinedev.practice.profile.rank.apis;

import xyz.refinedev.practice.profile.rank.RankType;
import me.quartz.hestia.HestiaAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class HestiaCore implements RankType {

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
    public boolean isBusy(OfflinePlayer player) {
        return HestiaAPI.instance.isVanished(player.getUniqueId());
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return HestiaAPI.instance.getRankColor(player.getUniqueId());
    }
}
