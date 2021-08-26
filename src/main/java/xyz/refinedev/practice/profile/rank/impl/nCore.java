package xyz.refinedev.practice.profile.rank.impl;

import me.absurd.ncore.NCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.profile.rank.RankAdapter;
import xyz.refinedev.practice.util.chat.ColourUtils;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/16/2021
 * Project: Array
 */

public class nCore implements RankAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return NCoreAPI.instance.getRank(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return NCoreAPI.instance.getPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return NCoreAPI.instance.getSuffix(player.getUniqueId());
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return getRankPrefix(player) + getRankColor(player) + player.getName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ColourUtils.format(NCoreAPI.instance.getNameColor(player.getUniqueId()));
    }
}
