package xyz.refinedev.practice.leaderboards.menu.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/6/2021
 * Project: Array
 */

public class ClanLeaderboardsButton extends Button {

    private final Array plugin = Array.getInstance();

    @Override
    public ItemStack getButtonItem(final Player player) {
        final List<String> lore = new ArrayList<>();

        int position = 1;
        lore.add(CC.MENU_BAR);
        for ( LeaderboardsAdapter leaderboardsAdapter : plugin.getLeaderboardsManager().getClanLeaderboards()) {
            lore.add(Locale.LEADERBOARDS_CLAN_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(position))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo())));
            ++position;
        }
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.GOLD_SWORD)
                .name(Locale.LEADERBOARDS_CLAN_HEADER.toString())
                .clearFlags()
                .lore(lore)
                .build();
    }
}
