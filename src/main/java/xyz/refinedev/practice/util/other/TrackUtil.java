package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/13/2021
 * Project: Array
 */

@UtilityClass
public class TrackUtil {

    public int getInQueues() {
        int inQueues = 0;

        for ( Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());

            if (profile.isInQueue()) {
                inQueues++;
            }
        }

        return inQueues;
    }

    public int getInFights() {
        int inFights = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());

            if (profile.isInFight() || profile.isInEvent()) {
                inFights++;
            }
        }

        return inFights;
    }

    public int getOnline() {
        return Bukkit.getOnlinePlayers().size();
    }
}
