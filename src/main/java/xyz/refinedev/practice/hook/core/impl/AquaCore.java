package xyz.refinedev.practice.hook.core.impl;

import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;

public class AquaCore implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? "" : data.getHighestRank().getName();
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? "" : data.getHighestRank().getPrefix();
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? "" : data.getHighestRank().getSuffix();
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return player.getPlayer().getDisplayName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId());
        return (data == null) ? ChatColor.GREEN : data.getHighestRank().getColor();
    }
}
