package me.drizzy.practice.api;

import lombok.Getter;
import me.drizzy.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrayCache {

    @Getter
    private static final Map<String, UUID> playerCache = new HashMap<>();
    @Getter
    private static final Map<UUID, String> nameCache = new HashMap<>();

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
        }
        return uuid;
    }

    public static String getName(UUID uuid) {
        String name = null;
        if (nameCache.containsKey(uuid)) {
            name = nameCache.get(uuid);
        }
        return name;
    }
}
