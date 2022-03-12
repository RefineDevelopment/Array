package xyz.refinedev.practice.hook.hologram.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.hook.hologram.HologramMeta;
import xyz.refinedev.practice.hook.hologram.PracticeHologram;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.managers.LeaderboardsManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.divisions.ProfileDivision;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class GlobalHologram extends PracticeHologram {

    private final Array plugin;

    /**
     * Spawn the hologram for all players on the server
     * at the given location in the constructor
     */
    public void spawn() {
        Preconditions.checkNotNull(this.meta, "Hologram Meta can not be null!");

        BasicConfigurationFile config = plugin.getHologramsConfig();
        ProfileManager profileManager = plugin.getProfileManager();
        LeaderboardsManager leaderboardsManager = plugin.getLeaderboardsManager();

        Hologram apiHologram = HologramsAPI.createHologram(plugin, meta.getLocation());
        apiHologram.clearLines();
        apiHologram.getVisibilityManager().setVisibleByDefault(true);
        if (!apiHologram.getLocation().getChunk().isLoaded()) {
            apiHologram.getLocation().getChunk().load();
        }

        for ( String line : config.getStringList("SETTINGS.DEFAULT.LINES") ) {
            if (line.contains("<top>")) {
                int position = 1;
                for ( LeaderboardsAdapter leaderboardsAdapter : leaderboardsManager.getGlobalLeaderboards()) {
                    Profile profile = profileManager.getProfile(leaderboardsAdapter.getUniqueId());
                    ProfileDivision division = profileManager.getDivision(profile);

                    apiHologram.appendTextLine(config.getString("SETTINGS.DEFAULT.FORMAT")
                            .replace("<number>", String.valueOf(position))
                            .replace("<value>", String.valueOf(leaderboardsAdapter.getElo()))
                            .replace("<name>", leaderboardsAdapter.getName())
                            .replace("<division>", division.getDisplayName()));
                    position++;
                }
                continue;
            }

            String replace = line.replace("<update>", String.valueOf(updateIn));

            apiHologram.appendTextLine(replace);
        }

        meta.setHologram(apiHologram);
    }

    /**
     * DeSpawn the hologram for all players on the server
     * This method will only deSpawn the hologram but not delete,
     * so after a restart it will be back to its original location
     */
    public void deSpawn() {
        Hologram hologram = meta.getHologram();
        hologram.clearLines();
        hologram.delete();
    }

    /**
     * Update the hologram and its contents
     * respectively, this will change the hologram's kit
     * in the {@link SwitchHologram} otherwise it will update
     * the leaderboard being displayed
     */
    public void update() {
        this.deSpawn();
        this.spawn();
    }
}
