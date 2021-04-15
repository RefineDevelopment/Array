package me.drizzy.practice.array.essentials;

import me.drizzy.practice.Array;
import me.drizzy.practice.array.essentials.event.SpawnTeleportEvent;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.location.LocationUtil;
import me.drizzy.practice.util.menu.MenuUpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Essentials {

    public static Location spawn;

    public Essentials() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Array.getInstance(), new MenuUpdateTask(), 5L, 5L);
        spawn = LocationUtil.deserialize(Array.getInstance().getMainConfig().getStringOrDefault("Array.Spawn", null));
    }

    public static void setSpawn(Location location) {
        spawn = location;

        Array.getInstance().getMainConfig().getConfiguration().set("Array.Spawn", LocationUtil.serialize(spawn));

        try {
            Array.getInstance().getMainConfig().getConfiguration().save(Array.getInstance().getMainConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void teleportToSpawn(Player player) {
        Location location = spawn;
        SpawnTeleportEvent event = new SpawnTeleportEvent(player, location);
        event.call();

        if (Profile.getByUuid(player) != null) {
            Profile.getByUuid(player).handleVisibility();
        }

        if (!event.isCancelled() && event.getLocation() != null) {
            player.teleport(event.getLocation());
        }
    }
}
