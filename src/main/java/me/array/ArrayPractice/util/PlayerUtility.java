package me.array.ArrayPractice.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerUtility
{
    public static Set<String> getConvertedUuidSet(Set<UUID> uuids) {
        Set<String> toReturn = new HashSet<>();

        for (UUID uuid : uuids) {
            toReturn.add(uuid.toString());
        }

        return toReturn;
    }

    public static List<Player> getOnlinePlayers()
    {
        ArrayList<Player> ret = new ArrayList<Player>();
        for (Player player : Bukkit.getServer().getOnlinePlayers() )
            ret.add(player);
        return ret;
    }
}

