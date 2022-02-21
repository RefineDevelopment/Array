package xyz.refinedev.practice.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.KothPoint;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 12/22/2021
 * Project: Array
 */

@Getter @Setter
public class EventLocations {

    //This class is mostly because access this in an instance seems quite pointless as
    //it does not have other uses besides checking and getting locations and knockback strings
    //besides, they are mostly nullable (most of the time) and they are loaded on startup so
    //no worries
    
    private final BasicConfigurationFile config;

    private Location sumoSpawn1, sumoSpawn2, sumoSpectator;
    private Location bracketsSpawn1, bracketsSpawn2, bracketsSpectator;
    private Location gulagSpawn1, gulagSpawn2, gulagSpectator;
    private Location lmsSpawn, parkourSpawn, spleefSpawn, omaSpawn;
    private Location kothSpawn1, kothSpawn2, kothSpec;
    private KothPoint kothPoint;

    private List<Location> OITCSpawns = new ArrayList<>();
    private Location OITCSpectator;

    private String sumoKB = "default", gulagKB = "default", omaKB = "default", spleefKB = "default", kothKB = "default";

    public EventLocations(BasicConfigurationFile config) {
        this.config = config;
    }
    
    public void loadLocations() {
        String key = "EVENTS.";

        if (config.contains(key + "SUMO.SPAWN1")) sumoSpawn1 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN1"));
        if (config.contains(key + "SUMO.SPAWN2")) sumoSpawn2 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN2"));
        if (config.contains(key + "SUMO.SPECTATOR")) sumoSpectator = LocationUtil.deserialize(config.getString(key + "SUMO.SPECTATOR"));
        if (config.contains(key + "SUMO.KNOCKBACK")) sumoKB = config.getString(key + "SUMO.KNOCKBACK");

        if (config.contains(key + "BRACKETS.SPAWN1")) bracketsSpawn1 = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPAWN1"));
        if (config.contains(key + "BRACKETS.SPAWN2")) bracketsSpawn2 = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPAWN2"));
        if (config.contains(key + "BRACKETS.SPECTATOR")) bracketsSpectator = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPECTATOR"));

        if (config.contains(key + "GULAG.SPAWN1")) gulagSpawn1 = LocationUtil.deserialize(config.getString(key + "GULAG.SPAWN1"));
        if (config.contains(key + "GULAG.SPAWN2")) gulagSpawn2 = LocationUtil.deserialize(config.getString(key + "GULAG.SPAWN2"));
        if (config.contains(key + "GULAG.SPECTATOR")) gulagSpectator = LocationUtil.deserialize(config.getString(key + "GULAG.SPECTATOR"));
        if (config.contains(key + "GULAG.KNOCKBACK")) gulagKB = config.getString(key + "GULAG.KNOCKBACK");

        if (config.contains(key + "LMS.SPAWN")) lmsSpawn = LocationUtil.deserialize(config.getString(key + "LMS.SPAWN"));

        if (config.contains(key + "PARKOUR.SPAWN")) parkourSpawn = LocationUtil.deserialize(config.getString(key + "PARKOUR.SPAWN"));

        if (config.contains(key + "SPLEEF.SPAWN")) spleefSpawn = LocationUtil.deserialize(config.getString(key + "SPLEEF.SPAWN"));
        if (config.contains(key + "SPLEEF.KNOCKBACK")) spleefKB = config.getString(key + "SPLEEF.KNOCKBACK");

        config.save();
    }

    public void save() {
        String key = "EVENTS.";

        if (sumoSpawn1 != null) config.set(key + "SUMO.SPAWN1", LocationUtil.serialize(sumoSpawn1));
        if (sumoSpawn2 != null) config.set(key + "SUMO.SPAWN2", LocationUtil.serialize(sumoSpawn2));
        if (sumoSpectator != null) config.set(key + "SUMO.SPECTATOR", LocationUtil.serialize(sumoSpectator));

        if (bracketsSpawn1 != null) config.set(key + "BRACKETS.SPAWN1", LocationUtil.serialize(bracketsSpawn1));
        if (bracketsSpawn2 != null) config.set(key + "BRACKETS.SPAWN2", LocationUtil.serialize(bracketsSpawn2));
        if (bracketsSpectator != null) config.set(key + "BRACKETS.SPECTATOR", LocationUtil.serialize(bracketsSpectator));

        if (gulagSpawn1 != null) config.set(key + "GULAG.SPAWN1", LocationUtil.serialize(gulagSpawn1));
        if (gulagSpawn2 != null) config.set(key + "GULAG.SPAWN2", LocationUtil.serialize(gulagSpawn2));
        if (gulagSpectator != null) config.set(key + "GULAG.SPECTATOR", LocationUtil.serialize(gulagSpectator));

        if (lmsSpawn != null) config.set(key + "LMS.SPAWN", LocationUtil.serialize(lmsSpawn));
        if (parkourSpawn != null) config.set(key + "PARKOUR.SPAWN", LocationUtil.serialize(parkourSpawn));
        if (spleefSpawn != null) config.set(key + "SPLEEF.SPAWN", LocationUtil.serialize(spleefSpawn));

        config.save();
    }


    public Location getSpawn1(Event event) {
        switch (event.getType()) {
            case SUMO:
                return sumoSpawn1;
            case BRACKETS:
                return bracketsSpawn1;
            case GULAG:
                return gulagSpawn1;
            case KOTH:
                return kothSpawn1;
        }
        return null;
    }

    public Location getSpawn2(Event event) {
        switch (event.getType()) {
            case SUMO:
                return sumoSpawn2;
            case BRACKETS:
                return bracketsSpawn2;
            case GULAG:
                return gulagSpawn2;
            case KOTH:
                return kothSpawn2;
        }
        return null;
    }

    public Location getSpawn(Event event) {
        switch (event.getType()) {
            case SPLEEF:
                return spleefSpawn;
            case PARKOUR:
                return parkourSpawn;
            case LMS:
                return lmsSpawn;
            case JUGGERNAUT:
                return omaSpawn;
        }
        return null;
    }

    public Location getSpectator(Event event) {
        switch (event.getType()) {
            case SUMO:
                return sumoSpectator;
            case BRACKETS:
                return bracketsSpectator;
            case GULAG:
                return gulagSpectator;
            case OITC:
                return OITCSpectator;
            case KOTH:
                return kothSpec;
        }
        return null;
    }

    public boolean isUnfinished(Event event) {
        if (event.isFreeForAll()) {
            return getSpawn(event) == null;
        } else {
            return getSpawn1(event) == null || getSpawn2(event) == null || getSpectator(event) == null;
        }
    }
}
