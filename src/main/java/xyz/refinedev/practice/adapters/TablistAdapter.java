package xyz.refinedev.practice.adapters;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.tablist.adapter.TabAdapter;
import xyz.refinedev.practice.util.tablist.entry.TabEntry;
import xyz.refinedev.practice.util.tablist.skin.Skin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/20/2021
 * Project: Array
 */

public class TablistAdapter implements TabAdapter {

    private final Array plugin = Array.getInstance();
    private final BasicConfigurationFile config = plugin.getTablistConfig();

    /**
     * Get the tab header for a player
     *
     * @param player the player
     * @return {@link String}
     */
    @Override
    public String getHeader(Player player) {
        return config.getString("TABLIST.HEADER");
    }

    /**
     * Get the tab footer for a player
     *
     * @param player the player
     * @return {@link String}
     */
    @Override
    public String getFooter(Player player) {
        return config.getString("TABLIST.FOOTER");
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
        Profile profile = Profile.getByPlayer(player); 
        
        if (profile.isInLobby() && profile.getParty() == null) {
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.LEFT." + i + 1);
                TabEntry entry = new TabEntry(3, i, CC.translate(replaceLobby(string)));

                if (hasDot(string)) entry.setSkin(getDot(string));
                if (hasSkin(string)) entry.setSkin(getSkin(player, string));

                entries.add(entry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.MIDDLE." + i + 1);
                TabEntry entry = new TabEntry(3, i, CC.translate(replaceLobby(string)));

                if (hasDot(string)) entry.setSkin(getDot(string));
                if (hasSkin(string)) entry.setSkin(getSkin(player, string));

                entries.add(entry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.RIGHT." + i + 1);
                TabEntry entry = new TabEntry(3, i, CC.translate(replaceLobby(string)));

                if (hasDot(string)) entry.setSkin(getDot(string));
                if (hasSkin(string)) entry.setSkin(getSkin(player, string));

                entries.add(entry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.FAR-RIGHT." + i + 1);
                TabEntry entry = new TabEntry(3, i, CC.translate(replaceLobby(string)));

                if (hasDot(string)) entry.setSkin(getDot(string));
                if (hasSkin(string)) entry.setSkin(getSkin(player, string));

                entries.add(entry);
            }
        }
        return entries;
    }
    
    public String replaceLobby(String toReplace) {
        toReplace = toReplace
                .replace("", "");
        
        return toReplace;
    }

    public boolean hasDot(String text) {
        return text.contains("<dot");
    }

    public Skin getDot(String text) {
        for ( ChatColor value : ChatColor.values() ) {
            if (text.contains("<dot_" + value.name().toLowerCase())) {
                return Skin.getDot(value);
            }
        }
        return Skin.DEFAULT_SKIN;
    }
    
    public boolean hasSkin(String text) {
        return text.contains("<your_player>") || text.contains("<opponent_player>") || text.contains("<skin_");
    }

    public Skin getSkin(Player player, String text) {
        if (text.contains("<your_player>"))  return Skin.getPlayer(player);
        if (text.contains("<skin_twitter>")) return Skin.TWITTER_SKIN;
        if (text.contains("<skin_website>")) return Skin.WEBSITE_SKIN;
        if (text.contains("<skin_discord>")) return Skin.DISCORD_SKIN;
        if (text.contains("<skin_youtube>")) return Skin.YOUTUBE_SKIN;

        if (text.contains("<opponent_player>")) {
            Profile profile = Profile.getByPlayer(player);
            if (profile.getMatch() != null) {
                return Skin.getPlayer(profile.getMatch().getOpponentPlayer(player));
            }
        }

        return Skin.DEFAULT_SKIN;
    }
}