package me.drizzy.practice.profile.rank.apis;

import com.broustudio.MizuAPI.MizuAPI;
import me.drizzy.practice.profile.rank.RankType;
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
}
