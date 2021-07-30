package xyz.refinedev.practice.api;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */

public interface API {

    boolean isInLobby(Player player);

    boolean isInParty(Player player);

    boolean isInFight(Player player);

    boolean isInEvent(Player player);

    boolean isInTournament(Player player);

    void handleVisibility(Player player);

    String getDisplayName(Player player);

    Profile getProfile(Player player);


}
