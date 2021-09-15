package xyz.refinedev.practice.hook.core.impl;

import com.broustudio.MizuAPI.MizuAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;
import xyz.refinedev.practice.util.chat.ColourUtils;

public class MizuCore implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return MizuAPI.getAPI().getRank(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return MizuAPI.getAPI().getRankPrefix(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return MizuAPI.getAPI().getRankSuffix(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return MizuAPI.getAPI().getRankPrefix(MizuAPI.getAPI().getRank(player.getUniqueId())) + player.getName();
    }


    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ColourUtils.format(MizuAPI.getAPI().getRankColor(MizuAPI.getAPI().getRank(player.getUniqueId())));
    }
}
