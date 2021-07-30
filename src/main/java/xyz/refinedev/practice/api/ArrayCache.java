package xyz.refinedev.practice.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.Profile;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */
public class ArrayCache {

    @Getter private static final Map<String, UUID> playerCache = new HashMap<>();

    public static int getInQueues() {
        int inQueues = 0;

        for ( Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.isInQueue()) {
                inQueues++;
            }
        }

        return inQueues;
    }

    public static int getInFights() {
        int inFights = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.isInFight() || profile.isInEvent()) {
                inFights++;
            }
        }

        return inFights;
    }

    public static int getOnline() {
        return Bukkit.getOnlinePlayers().size();
    }

    public static UUID getUUID(String name) {
        UUID uuid = null;
        if (playerCache.containsKey(name)) {
            uuid = playerCache.get(name);
        } else if (Bukkit.getOfflinePlayer(name) != null) {
            return Bukkit.getOfflinePlayer(name).getUniqueId();
        }
        return uuid;
    }

    
}
