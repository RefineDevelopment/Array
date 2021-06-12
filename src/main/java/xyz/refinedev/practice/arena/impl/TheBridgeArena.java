package xyz.refinedev.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.location.LocationUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 3/21/2021
 * Project: Array
 */


@Getter @Setter
public class TheBridgeArena extends Arena {

    private Cuboid redCuboid;
    private Cuboid blueCuboid;
    private Cuboid redPortal;
    private Cuboid bluePortal;

    public TheBridgeArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.THEBRIDGE;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        FileConfiguration configuration = Array.getInstance().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());
        configuration.set(path + ".display-name", CC.untranslate(getDisplayName()));
        configuration.set(path + ".icon.material", displayIcon.getType().name());
        configuration.set(path + ".icon.durability", displayIcon.getDurability());
        configuration.set(path + ".disable-pearls", disablePearls);

        if (spawn1 != null) {
            configuration.set(path + ".spawn1", LocationUtil.serialize(spawn1));
        }

        if (spawn2 != null) {
            configuration.set(path + ".spawn2", LocationUtil.serialize(spawn2));
        }

        if (max != null) {
            configuration.set(path + ".max", LocationUtil.serialize(max));
        }

        if (min != null) {
            configuration.set(path + ".min", LocationUtil.serialize(min));
        }

        configuration.set(path + ".kits", getKits());

        if (redCuboid != null) {
            configuration.set(path + ".redCuboid.location1", LocationUtil.serialize(redCuboid.getLowerCorner()));
            configuration.set(path + ".redCuboid.location2", LocationUtil.serialize(redCuboid.getUpperCorner()));
        }
        if (redPortal != null) {
            configuration.set(path + ".redPortal.location1", LocationUtil.serialize(redPortal.getLowerCorner()));
            configuration.set(path + ".redPortal.location2", LocationUtil.serialize(redPortal.getUpperCorner()));
        }
        if (blueCuboid != null) {
            configuration.set(path + ".blueCuboid.location1", LocationUtil.serialize(blueCuboid.getLowerCorner()));
            configuration.set(path + ".blueCuboid.location2", LocationUtil.serialize(blueCuboid.getUpperCorner()));
        }
        if (bluePortal != null) {
            configuration.set(path + ".bluePortal.location1", LocationUtil.serialize(bluePortal.getLowerCorner()));
            configuration.set(path + ".bluePortal.location2", LocationUtil.serialize(bluePortal.getUpperCorner()));
        }

        try {
            configuration.save(Array.getInstance().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        FileConfiguration configuration = Array.getInstance().getArenasConfig().getConfiguration();
        configuration.set("arenas." + getName(), null);

        try {
            configuration.save(Array.getInstance().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSetup() {
        return spawn1 != null && spawn2 != null && max != null && min != null && blueCuboid != null && redCuboid != null && redPortal != null && bluePortal != null;
    }

}
