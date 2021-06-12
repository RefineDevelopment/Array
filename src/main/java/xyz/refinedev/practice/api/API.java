package xyz.refinedev.practice.api;

import xyz.refinedev.practice.profile.Profile;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */

public interface API {

    public boolean isInLobby(Player player);

    public boolean isInParty(Player player);

    public boolean isInFight(Player player);

    public boolean isInEvent(Player player);

    public boolean isInTournament(Player player);

    public void handleVisibility(Player player);

    public String getDisplayName(Player player);

    public Profile getProfile(Player player);


}
