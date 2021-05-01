package me.drizzy.practice.profile.rank;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public interface RankType {

    String getRankName(OfflinePlayer player);

    String getRankPrefix(OfflinePlayer player);

    String getRankSuffix(OfflinePlayer player);

    String getFullName(OfflinePlayer player);

    ChatColor getRankColor(OfflinePlayer player);
}
