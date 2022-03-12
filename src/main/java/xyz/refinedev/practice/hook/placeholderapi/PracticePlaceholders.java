package xyz.refinedev.practice.hook.placeholderapi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

//TODO: Complete this
@RequiredArgsConstructor
public class PracticePlaceholders extends PlaceholderExpansion {

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
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player);

        switch (identifier) {
            case "%array_profile_name%": return profile.getName();
            case "%array_profile_kill_effect%": return profile.getKillEffect().getDisplayName();
            case "%array_profile_division%": return profileManager.getDivision(profile).getDisplayName();
            case "%array_profile_state%": return profile.getState().name();
            case "%array_profile_global_elo%": return String.valueOf(profile.getGlobalElo());
            case "%array_profile_experience%": return String.valueOf(profile.getExperience());
            case "%array_profile_deaths%": return String.valueOf(profile.getDeaths());
            case "%array_profile_kills%": return String.valueOf(profile.getKills());
            case "%array_profile_wins%": return String.valueOf(profile.getTotalWins());
            case "%array_profile_losses%": return String.valueOf(profile.getTotalLost());
        }

        return null;
    }
}
