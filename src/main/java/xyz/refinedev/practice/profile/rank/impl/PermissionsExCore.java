package xyz.refinedev.practice.profile.rank.impl;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import xyz.refinedev.practice.profile.rank.RankAdapter;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/16/2021
 * Project: Array
 */

public class PermissionsExCore implements RankAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return PermissionsEx.getUser(player.getPlayer()).getName();
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return PermissionsEx.getUser(player.getPlayer()).getPrefix();
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return PermissionsEx.getUser(player.getPlayer()).getSuffix();
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return getRankPrefix(player) + player.getName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ChatColor.GREEN;
    }
}
