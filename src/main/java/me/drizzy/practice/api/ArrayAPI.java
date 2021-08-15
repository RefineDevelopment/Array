package me.drizzy.practice.api;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

public class ArrayAPI {

    public static boolean isInLobby(Player player) {
        Profile p = Profile.getByUuid(player.getUniqueId());
        if (p.isInLobby())
            return true;
        if (p.isInQueue())
            return true;
        return !p.isInFight() && !p.isInEvent() && !p.isSpectating() && !p.isInTournament();
    }

    public static void handleVisibility(Player player) {
        Profile p = Profile.getByUuid(player.getUniqueId());
        p.handleVisibility();
    }

    public static String getDisplayName(Player player) {
      return Array.getInstance().getRankManager().getFullName(player);
    }

    public static boolean isInParty(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.getParty() != null;
    }

    public static boolean isInTournament(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.isInTournament();
    }

    public static boolean isInEvent(Player player) {
        Profile p = Profile.getByPlayer(player);
        return p.isInEvent();
    }

    public static Profile getProfile(Player player) {
        return Profile.getByPlayer(player);
    }

}
