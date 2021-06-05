package me.drizzy.practice.profile.rank.apis;

import com.broustudio.MizuAPI.MizuAPI;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.util.chat.ColourUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class MizuCore implements RankType {

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
    public boolean isBusy(OfflinePlayer player) {
        return false;
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ColourUtils.format(MizuAPI.getAPI().getRankColor(MizuAPI.getAPI().getRank(player.getUniqueId())));
    }
}
