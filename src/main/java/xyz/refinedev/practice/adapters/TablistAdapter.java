package xyz.refinedev.practice.adapters;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.TabManager;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.tablist.adapter.TabAdapter;
import xyz.refinedev.practice.util.tablist.entry.TabEntry;
import xyz.refinedev.practice.util.tablist.skin.Skin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/20/2021
 * Project: Array
 */

public class TablistAdapter implements TabAdapter {

    private final TabManager manager = Array.getInstance().getTabManager();
    private final Array plugin = Array.getInstance();

    /**
     * Get the tab header for a player
     *
     * @param player the player
     * @return {@link String}
     */
    @Override
    public String getHeader(Player player) {
        return manager.getHeader();
    }

    /**
     * Get the tab footer for a player
     *
     * @param player the player
     * @return {@link String}
     */
    @Override
    public String getFooter(Player player) {
        return manager.getFooter();
    }

    /**
     * Get the tab lines for a player.
     *
     * @param player The player viewing the tablist
     * @return {@link List<TabEntry>}
     */
    @Override
    public List<TabEntry> getLines(Player player) {
        List<TabEntry> entries = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            final int x = i % 4;
            final int y = i / 4;

            entries.add(new TabEntry(x, y, ChatColor.GREEN + "Slot: " + ChatColor.GRAY + x + ", " + y, PlayerUtil.getPing(player), Skin.getPlayer(player)));
        }
        return entries;
    }
}
