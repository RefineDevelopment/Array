package xyz.refinedev.practice.profile.rank;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public interface RankAdapter {

    String getRankName(OfflinePlayer player);

    String getRankPrefix(OfflinePlayer player);

    String getRankSuffix(OfflinePlayer player);

    String getFullName(OfflinePlayer player);

    ChatColor getRankColor(OfflinePlayer player);
}
