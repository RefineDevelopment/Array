package xyz.refinedev.practice.leaderboards.external;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.ArrayCache;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.kit.Kit;

public class LeaderboardPlaceholders extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "array";
    }

    @Override
    public String getAuthor() {
        return "Drizzy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "&7";
        }

        /*
         * Originally coded by Nick and improved by Drizzy.
         * Note: The Player Cache in profile should not be null for it to work!
         *
         * GlobalLeaderboards - array_global_<position>
         */

        if (identifier.contains("global")) {
            String[] splitstring = identifier.split("_");
            int number = Integer.parseInt(splitstring[1]) - 1;
            LeaderboardsAdapter leaderboardsAdapter;

            try {
                leaderboardsAdapter = Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) {
                return "&7";
            }

            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));

            return Locale.LEADERBOARDS_GLOBAL_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", ChatColor.stripColor(profile.getEloLeague()));
        }

        /*
         * Originally coded by Nick and improved by Drizzy.
         * Note: The Player Cache in profile should not be null for it to work!
         *
         * LeaderboardsAdapter - array_leaderboards_<Kit>_<position>
         */
        if (identifier.contains("leaderboards")) {
            String[] splitstring = identifier.split("_");
            String kitString = splitstring[1];
            int number = Integer.parseInt(splitstring[2]) - 1;
            Kit kit = Kit.getByName(kitString);

            if (kit == null) return "&7Error, That kit does not exist!";

            LeaderboardsAdapter leaderboardsAdapter;

            try {
                leaderboardsAdapter = kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) {
                return "&7";
            }

            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));

            return Locale.LEADERBOARDS_KIT_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", profile.getEloLeague());

        }
        return null;
    }
}
