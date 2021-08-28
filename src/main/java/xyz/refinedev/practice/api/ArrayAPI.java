package xyz.refinedev.practice.api;

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
public class ArrayAPI implements API {

    @Override
    public boolean isInLobby(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInLobby() || profile.isInQueue()) return true;
        return !profile.isInFight() && !profile.isInEvent() && !profile.isSpectating() && !profile.isInTournament();
    }

    @Override
    public boolean isInParty(Player player) {
        Profile profile = Profile.getByPlayer(player);
        return profile.getParty() != null;
    }

    @Override
    public boolean isInFight(Player player) {
        Profile profile = Profile.getByPlayer(player);
        return profile.isInFight();
    }

    @Override
    public boolean isInTournament(Player player) {
        Profile profile = Profile.getByPlayer(player);
        return profile.isInTournament();
    }

    @Override
    public boolean isInEvent(Player player) {
        Profile profile = Profile.getByPlayer(player);
        return profile.isInEvent();
    }

    @Override
    public void handleVisibility(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.handleVisibility();
    }

    @Override
    public String getDisplayName(Player player) {
        return Array.getInstance().getRankManager().getRankType().getRankAdapter().getFullName(player);
    }

    @Override
    public Profile getProfile(Player player) {
        return Profile.getByPlayer(player);
    }

}
