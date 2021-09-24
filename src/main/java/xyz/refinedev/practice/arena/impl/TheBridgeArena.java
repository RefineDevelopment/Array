package xyz.refinedev.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.meta.cuboid.Cuboid;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 3/21/2021
 * Project: Array
 */


@Getter @Setter
public class TheBridgeArena extends Arena {

    private final List<Arena> duplicates = new ArrayList<>();

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

        if (!duplicates.isEmpty()) {
            int i = 0;

            for (Arena duplicate : duplicates) {
                i++;

                configuration.set(path + ".duplicates." + i + ".spawn1", LocationUtil.serialize(duplicate.getSpawn1()));
                configuration.set(path + ".duplicates." + i + ".spawn2", LocationUtil.serialize(duplicate.getSpawn2()));
                configuration.set(path + ".duplicates." + i + ".max", LocationUtil.serialize(duplicate.getMax()));
                configuration.set(path + ".duplicates." + i + ".min", LocationUtil.serialize(duplicate.getMin()));

                if (duplicate.getRedCuboid() != null) {
                    configuration.set(path + ".duplicates." + i + ".redCuboid.location1", LocationUtil.serialize(duplicate.getRedCuboid().getLowerCorner()));
                    configuration.set(path + ".duplicates." + i + ".redCuboid.location2", LocationUtil.serialize(duplicate.getRedCuboid().getUpperCorner()));
                }
                if (duplicate.getRedPortal() != null) {
                    configuration.set(path + ".duplicates." + i + ".redPortal.location1", LocationUtil.serialize(duplicate.getRedPortal().getLowerCorner()));
                    configuration.set(path + ".duplicates." + i + ".redPortal.location2", LocationUtil.serialize(duplicate.getRedPortal().getUpperCorner()));
                }
                if (duplicate.getBlueCuboid() != null) {
                    configuration.set(path + ".duplicates." + i + ".blueCuboid.location1", LocationUtil.serialize(duplicate.getBlueCuboid().getLowerCorner()));
                    configuration.set(path + ".duplicates." + i + ".blueCuboid.location2", LocationUtil.serialize(duplicate.getBlueCuboid().getUpperCorner()));
                }
                if (duplicate.getBluePortal() != null) {
                    configuration.set(path + ".duplicates." + i + ".bluePortal.location1", LocationUtil.serialize(duplicate.getBluePortal().getLowerCorner()));
                    configuration.set(path + ".duplicates." + i + ".bluePortal.location2", LocationUtil.serialize(duplicate.getBluePortal().getUpperCorner()));
                }
            }
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
