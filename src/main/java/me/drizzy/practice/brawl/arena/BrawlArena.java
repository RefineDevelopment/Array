package me.drizzy.practice.brawl.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/26/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class BrawlArena {

    public static final Array plugin = Array.getInstance();

    public final String name;
    public boolean enabled;

    public Location spawn1;
    public Location spawn2;

    public boolean isComplete() {
        return spawn1 != null && spawn2 != null;
    }

/*    public void save() {
        Configuration config = plugin.getBrawlConfig().getConfiguration();

        String key = "ARENAS.";

        config.set(key + "NAME", name);
        config.set(key + "ENABLED", enabled);
        config.set(key + "SPAWN1", LocationUtil.serialize(spawn1));
        config.set(key + "SPAWN2", LocationUtil.serialize(spawn2));

        plugin.getBrawlConfig().save();
    }*/

    /*public static BrawlArena getByName(String name) {
        for ( BrawlArena arena : BrawlManager.getArenas() ) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }*/


}
