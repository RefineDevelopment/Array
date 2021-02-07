package me.drizzy.practice.util.essentials;

import me.drizzy.practice.util.bootstrap.Bootstrapped;
import me.drizzy.practice.util.essentials.event.SpawnTeleportEvent;
import me.drizzy.practice.util.external.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Essentials extends Bootstrapped {

    private Location spawn;

    public Essentials(me.drizzy.practice.Array Array) {
        super(Array);

        spawn = LocationUtil.deserialize(Array.getMainConfig().getStringOrDefault("ARRAY.SPAWN", null));
    }

    public void setSpawn(Location location) {
        spawn = location;

        Array.getMainConfig().getConfiguration().set("ARRAY.SPAWN", LocationUtil.serialize(this.spawn));

        try {
            Array.getMainConfig().getConfiguration().save(Array.getMainConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void teleportToSpawn(Player player) {
        Location location = spawn;

        SpawnTeleportEvent event = new SpawnTeleportEvent(player, location);
        event.call();

        if (!event.isCancelled() && event.getLocation() != null) {
            player.teleport(event.getLocation());
        }
    }

    public void clearEntities(World world) {
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.PLAYER) {
                continue;
            }
            entity.remove();
        }
    }

}
