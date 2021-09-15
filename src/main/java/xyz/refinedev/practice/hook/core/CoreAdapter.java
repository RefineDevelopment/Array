package xyz.refinedev.practice.hook.core;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public interface CoreAdapter {

    String getRankName(OfflinePlayer player);

    String getRankPrefix(OfflinePlayer player);

    String getRankSuffix(OfflinePlayer player);

    String getFullName(OfflinePlayer player);

    ChatColor getRankColor(OfflinePlayer player);
}
