package xyz.refinedev.practice.hook.placeholderapi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;

@RequiredArgsConstructor
public class LeaderboardPlaceholders extends PlaceholderExpansion {

    private final Array plugin;

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
            String[] splitString = identifier.split("_");

            //We subtract 1 because of lists starting with 0
            int number = Integer.parseInt(splitString[1]) - 1;

            LeaderboardsAdapter leaderboardsAdapter;
            try {
                leaderboardsAdapter = plugin.getLeaderboardsManager().getGlobalLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) return "&7";

            Profile profile = plugin.getProfileManager().getProfile(leaderboardsAdapter.getUuid());

            return Locale.LEADERBOARDS_GLOBAL_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", plugin.getProfileManager().getDivision(profile).getDisplayName());
        }

        if (identifier.contains("leaderboards")) {
            String[] splitString = identifier.split("_");

            //We subtract 1 because of lists starting with 0
            String kitName = splitString[1];
            int number = Integer.parseInt(splitString[2]) - 1;
            Kit kit = plugin.getKitManager().getByName(kitName);

            if (kit == null) return "&7Error, That kit does not exist!";

            LeaderboardsAdapter leaderboardsAdapter;
            try {
                leaderboardsAdapter = kit.getEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) return "&7";

            Profile profile = plugin.getProfileManager().getProfile(leaderboardsAdapter.getUuid());

            return Locale.LEADERBOARDS_KIT_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(number + 1))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", plugin.getProfileManager().getDivision(profile).getDisplayName());

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
