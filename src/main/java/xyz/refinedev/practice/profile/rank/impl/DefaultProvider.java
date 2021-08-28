package xyz.refinedev.practice.profile.rank.impl;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.profile.rank.RankAdapter;
import xyz.refinedev.practice.util.chat.CC;

public class DefaultProvider implements RankAdapter {

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
