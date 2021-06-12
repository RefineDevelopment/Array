package xyz.refinedev.practice.profile.rank.apis;

import xyz.refinedev.practice.profile.rank.RankType;
import xyz.refinedev.practice.util.chat.CC;
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
    public boolean isBusy(OfflinePlayer player) {
        return false;
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ChatColor.GREEN;
    }
}
