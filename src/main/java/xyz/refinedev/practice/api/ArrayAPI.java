package xyz.refinedev.practice.api;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ArrayAPI implements API {

    private final Array plugin;

    @Override
    public boolean isInLobby(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (profile.isInLobby() || profile.isInQueue()) return true;
        return !profile.isInFight() && !profile.isInEvent() && !profile.isSpectating() && !profile.isInTournament();
    }

    @Override
    public boolean isInParty(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        return profile.hasParty();
    }

    @Override
    public boolean isInFight(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        return profile.isInFight();
    }

    @Override
    public boolean isInTournament(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        return profile.isInTournament();
    }

    @Override
    public boolean isInEvent(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        return profile.isInEvent();
    }

    @Override
    public void handleVisibility(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        plugin.getProfileManager().handleVisibility(profile);
    }

    @Override
    public String getDisplayName(Player player) {
        return plugin.getCoreHandler().getFullName(player);
    }

    @Override
    public Profile getProfile(Player player) {
        return plugin.getProfileManager().getProfileByPlayer(player);
    }

}
