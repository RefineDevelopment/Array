package xyz.refinedev.practice.api;

import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.rank.Rank;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */
public class ArrayAPI implements API {

    @Override
    public boolean isInLobby(Player player) {
        Profile p = Profile.getByUuid(player.getUniqueId());
        if (p.isInLobby())
            return true;
        if (p.isInQueue())
            return true;
        return !p.isInFight() && !p.isInEvent() && !p.isSpectating() && !p.isInTournament();
    }

    @Override
    public boolean isInParty(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.getParty() != null;
    }

    @Override
    public boolean isInFight(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.isInFight();
    }

    @Override
    public boolean isInTournament(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.isInTournament();
    }

    @Override
    public boolean isInEvent(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.isInEvent();
    }

    @Override
    public void handleVisibility(Player player) {
        Profile p = Profile.getByUuid(player.getUniqueId());
        p.handleVisibility();
    }

    @Override
    public String getDisplayName(Player player) {
        return Rank.getRankType().getFullName(player);
    }

    @Override
    public Profile getProfile(Player player) {
        return Profile.getByPlayer(player);
    }

}
