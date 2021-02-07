package me.drizzy.practice.util;

import me.drizzy.practice.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class KothPoint {

    @Getter
    @Setter
    private Location corner1, corner2;

    @Getter
    private Cuboid cuboid;

    public KothPoint(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public static KothPoint fromString(String input) {
        String[] info = input.split(":");
        Location loc1 = LocationUtil.deserialize(info[0]);
        Location loc2 = LocationUtil.deserialize(info[1]);
        return new KothPoint(loc1, loc2);
    }

    public Cuboid toCuboid() {
        if (corner1 == null || corner2 == null)
            return null;
        if (cuboid == null)
            cuboid = new Cuboid(corner1, corner2);

        return cuboid;
    }

    public void reformCuboid() {
        if (corner1 == null || corner2 == null)
            return;

        cuboid = new Cuboid(corner1, corner2);
    }

    @Override
    public String toString() {
        if (corner1 != null && corner2 != null)
            return LocationUtil.serialize(corner1) + ":" + LocationUtil.serialize(corner2) + ":";

        return null;
    }
}
