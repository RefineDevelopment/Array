package me.array.ArrayPractice.util.essentials;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.bootstrap.Bootstrapped;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.util.essentials.event.SpawnTeleportEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Essentials extends Bootstrapped {

    private Location spawn;

    public Essentials(Practice Practice) {
        super(Practice);

        spawn = LocationUtil.deserialize(Practice.getMainConfig().getStringOrDefault("ARRAY.SPAWN", null));
    }

    public void setSpawn(Location location) {
        spawn = location;

        Practice.getMainConfig().getConfiguration().set("ARRAY.SPAWN", LocationUtil.serialize(this.spawn));

        try {
            Practice.getMainConfig().getConfiguration().save(Practice.getMainConfig().getFile());
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
