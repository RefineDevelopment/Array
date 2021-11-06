package xyz.refinedev.practice.util.location;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.refinedev.practice.arena.cuboid.Cuboid;

@Getter @Setter
public class KothPoint {

    private Location corner1, corner2;
    private Cuboid cuboid;

    public KothPoint(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public KothPoint(String input) {
        String[] info = input.split(":");
        this.corner1 = LocationUtil.deserialize(info[0]);
        this.corner2 = LocationUtil.deserialize(info[1]);
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
