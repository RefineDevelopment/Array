package me.drizzy.practice.util;

import me.activated.core.plugin.AquaCoreAPI;
import me.activated.core.api.rank.RankData;
import org.bukkit.entity.Player;

public class AquaCoreHook {

    AquaCoreAPI aquaCoreAPI;

    public AquaCoreHook() {
        this.aquaCoreAPI = AquaCoreAPI.INSTANCE;
    }

    public String getPlayerPrefix(Player player) {
        RankData rankData = aquaCoreAPI.getPlayerRank(player.getUniqueId());
        return rankData.getPrefix();
    }

    public String getPlayerColor(Player player) {
        RankData rankData = aquaCoreAPI.getPlayerRank(player.getUniqueId());
        return rankData.getDisplayColor() + player.getName();
    }

}
