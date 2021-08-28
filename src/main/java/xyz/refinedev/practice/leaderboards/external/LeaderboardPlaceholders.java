package xyz.refinedev.practice.leaderboards.external;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;

public class LeaderboardPlaceholders extends PlaceholderExpansion {

    private final Array plugin = Array.getInstance();

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
        return "2.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "&7";

        if (identifier.contains("global")) {
            String[] splitstring = identifier.split("_");

            //We subtract 1 because of lists being in whole numbers instead of natural numbers
            int number = Integer.parseInt(splitstring[1]) - 1;

            LeaderboardsAdapter leaderboardsAdapter;
            try {
                leaderboardsAdapter = plugin.getLeaderboardsManager().getGlobalLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) return "&7";

            Profile profile = Profile.getByUuid(leaderboardsAdapter.getUuid());

            return Locale.LEADERBOARDS_GLOBAL_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", ChatColor.stripColor(profile.getDivision()));
        }

        if (identifier.contains("leaderboards")) {
            String[] splitstring = identifier.split("_");

            //We subtract 1 because of lists being in whole numbers instead of natural numbers
            String kitname = splitstring[1];
            int number = Integer.parseInt(splitstring[2]) - 1;
            Kit kit = Kit.getByName(kitname);

            if (kit == null) return "&7Error, That kit does not exist!";

            LeaderboardsAdapter leaderboardsAdapter;

            try {
                leaderboardsAdapter = kit.getEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) return "&7";

            Profile profile = Profile.getByUuid(leaderboardsAdapter.getUuid());

            return Locale.LEADERBOARDS_KIT_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", profile.getDivision());

        }
        if (identifier.contains("clan")) {
            String[] splitstring = identifier.split("_");

            //We subtract 1 because of lists being in whole numbers instead of natural numbers
            int number = Integer.parseInt(splitstring[1]) - 1;

            LeaderboardsAdapter leaderboardsAdapter;
            try {
                leaderboardsAdapter = plugin.getLeaderboardsManager().getClanLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) return "&7";

            return Locale.LEADERBOARDS_CLAN_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()));
        }
        return null;
    }
}
