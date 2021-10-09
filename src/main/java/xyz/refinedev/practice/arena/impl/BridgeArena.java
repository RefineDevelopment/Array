package xyz.refinedev.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 3/21/2021
 * Project: Array
 */


@Getter @Setter
public class BridgeArena extends Arena {

    private final Array plugin;
    private final List<BridgeArena> duplicates = new ArrayList<>();

    private Cuboid redCuboid, blueCuboid, redPortal, bluePortal;

    public BridgeArena(Array plugin, String name) {
        super(plugin, name, ArenaType.BRIDGE);

        this.plugin = plugin;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        BasicConfigurationFile config = plugin.getArenasConfig();
        config.set(path, null);
        config.set(path + ".type", this.getType().name());
        config.set(path + ".display-name", CC.untranslate(this.getDisplayName()));

        if (this.getSpawn1() != null) config.set(path + ".spawn1", LocationUtil.serialize(this.getSpawn1()));
        if (this.getSpawn2() != null) config.set(path + ".spawn2", LocationUtil.serialize(this.getSpawn2()));
        if (this.getMax() != null) config.set(path + ".max", LocationUtil.serialize(this.getMax()));
        if (this.getMin() != null) config.set(path + ".min", LocationUtil.serialize(this.getMin()));

        if (redCuboid != null) {
            config.set(path + ".redCuboid.location1", LocationUtil.serialize(redCuboid.getLowerCorner()));
            config.set(path + ".redCuboid.location2", LocationUtil.serialize(redCuboid.getUpperCorner()));
        }
        if (redPortal != null) {
            config.set(path + ".redPortal.location1", LocationUtil.serialize(redPortal.getLowerCorner()));
            config.set(path + ".redPortal.location2", LocationUtil.serialize(redPortal.getUpperCorner()));
        }
        if (blueCuboid != null) {
            config.set(path + ".blueCuboid.location1", LocationUtil.serialize(blueCuboid.getLowerCorner()));
            config.set(path + ".blueCuboid.location2", LocationUtil.serialize(blueCuboid.getUpperCorner()));
        }
        if (bluePortal != null) {
            config.set(path + ".bluePortal.location1", LocationUtil.serialize(bluePortal.getLowerCorner()));
            config.set(path + ".bluePortal.location2", LocationUtil.serialize(bluePortal.getUpperCorner()));
        }

        config.set(path + ".disable-pearls", this.isDisablePearls());
        config.set(path + ".fall-death-height", this.getDeathHeight());
        config.set(path + ".icon.material", this.getDisplayIcon().getType().name());
        config.set(path + ".icon.durability", this.getDisplayIcon().getDurability());
        config.set(path + ".kits", getKits().stream().map(Kit::getName).collect(Collectors.toList()));

        config.set(path + ".kits", getKits());

        if (!duplicates.isEmpty()) {
            int i = 0;

            for ( BridgeArena duplicate : duplicates) {
                i++;

                config.set(path + ".duplicates." + i + ".spawn1", LocationUtil.serialize(duplicate.getSpawn1()));
                config.set(path + ".duplicates." + i + ".spawn2", LocationUtil.serialize(duplicate.getSpawn2()));
                config.set(path + ".duplicates." + i + ".max", LocationUtil.serialize(duplicate.getMax()));
                config.set(path + ".duplicates." + i + ".min", LocationUtil.serialize(duplicate.getMin()));

                if (duplicate.getRedCuboid() != null) {
                    config.set(path + ".duplicates." + i + ".redCuboid.location1", LocationUtil.serialize(duplicate.getRedCuboid().getLowerCorner()));
                    config.set(path + ".duplicates." + i + ".redCuboid.location2", LocationUtil.serialize(duplicate.getRedCuboid().getUpperCorner()));
                }
                if (duplicate.getRedPortal() != null) {
                    config.set(path + ".duplicates." + i + ".redPortal.location1", LocationUtil.serialize(duplicate.getRedPortal().getLowerCorner()));
                    config.set(path + ".duplicates." + i + ".redPortal.location2", LocationUtil.serialize(duplicate.getRedPortal().getUpperCorner()));
                }
                if (duplicate.getBlueCuboid() != null) {
                    config.set(path + ".duplicates." + i + ".blueCuboid.location1", LocationUtil.serialize(duplicate.getBlueCuboid().getLowerCorner()));
                    config.set(path + ".duplicates." + i + ".blueCuboid.location2", LocationUtil.serialize(duplicate.getBlueCuboid().getUpperCorner()));
                }
                if (duplicate.getBluePortal() != null) {
                    config.set(path + ".duplicates." + i + ".bluePortal.location1", LocationUtil.serialize(duplicate.getBluePortal().getLowerCorner()));
                    config.set(path + ".duplicates." + i + ".bluePortal.location2", LocationUtil.serialize(duplicate.getBluePortal().getUpperCorner()));
                }
            }
        }

        config.save();
    }

    @Override
    public boolean isSetup() {
        return this.getSpawn1() != null && this.getSpawn2() != null && this.getMax() != null && this.getMin() != null && blueCuboid != null && redCuboid != null && redPortal != null && bluePortal != null;
    }

}
