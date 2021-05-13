package me.drizzy.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.cuboid.Cuboid;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.location.LocationUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter
@Setter
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

}
