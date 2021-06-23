package xyz.refinedev.practice.adapters;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.tablist.adapter.TabAdapter;
import xyz.refinedev.practice.util.tablist.entry.TabEntry;
import xyz.refinedev.practice.util.tablist.skin.Skin;

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

                TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.MIDDLE." + i + 1);

                TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.RIGHT." + i + 1);

                TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.FAR-RIGHT." + i + 1);

                TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
        }
        return entries;
    }
    
    public String replaceLobby(Player player, String toReplace) {
        toReplace = toReplace
                .replace("", "");
        
        return toReplace;
    }
    
    public TabEntry checkSkin(Player player, TabEntry entry) {
        String text = entry.getText();

        if (text.contains("<opponent_player>")) {
            Profile profile = Profile.getByPlayer(player);
            if (profile.getMatch() != null) {
                entry.setSkin(Skin.getPlayer(profile.getMatch().getOpponentPlayer(player)));
            }
        }
        if (text.contains("<your_player>")) {
            entry.setSkin(Skin.getPlayer(player));
        }
        if (text.contains("<skin_twitter>")) {
            entry.setSkin(Skin.TWITTER_SKIN);
            text = text.replace("<skin_twitter>", "");
        }
        if (text.contains("<skin_website>")) {
            entry.setSkin(Skin.WEBSITE_SKIN);
            text = text.replace("<skin_website>", "");
        }
        if (text.contains("<skin_discord>")) {
            entry.setSkin(Skin.DISCORD_SKIN);
            text = text.replace("<skin_discord>", "");
        }
        if (text.contains("<skin_youtube>")) {
            entry.setSkin(Skin.YOUTUBE_SKIN);
            text = text.replace("<skin_youtube>", "");
        }
        entry.setText(text);
        return entry;
    }

    public TabEntry checkDot(TabEntry entry) {
        String text = entry.getText();
        for ( ChatColor value : ChatColor.values() ) {
            if (text.contains("<dot_" + value.name().toLowerCase())) {
                entry.setSkin(Skin.getDot(value));
                text = text.replace("<dot_" + value.name().toLowerCase(), "");
            }
        }
        entry.setText(text);
        return entry;
    }
}