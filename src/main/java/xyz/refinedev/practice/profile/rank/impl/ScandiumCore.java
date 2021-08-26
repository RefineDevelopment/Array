package xyz.refinedev.practice.profile.rank.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
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

public class ScandiumCore implements RankAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        Grant grant = potPlayer.getActiveGrant();
        return grant.getRank().getName();
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        Grant grant = potPlayer.getActiveGrant();
        return grant.getRank().getPrefix();
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        Grant grant = potPlayer.getActiveGrant();
        return grant.getRank().getSuffix();
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return getRankPrefix(player) + getRankColor(player) + player.getName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        Grant grant = potPlayer.getActiveGrant();
        return ColourUtils.format(grant.getRank().getColor());
    }
}
