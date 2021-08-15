package me.drizzy.practice.profile.rank.apis;

import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class DefaultProvider implements RankType {

    @Override
    public String getRankName(OfflinePlayer player) {
        return CC.translate("&a");
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return CC.translate("&a");
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return CC.translate("&a");
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return player.getPlayer().getDisplayName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ChatColor.GREEN;
    }
}
