package xyz.refinedev.practice.arena.impl;

import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.location.LocationUtil;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class StandaloneArena extends Arena {

    private final List<Arena> duplicates = new ArrayList<>();

    public StandaloneArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.STANDALONE;
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

        if (!duplicates.isEmpty()) {
            int i = 0;

            for (Arena duplicate : duplicates) {
                i++;

                configuration.set(path + ".duplicates." + i + ".spawn1", LocationUtil.serialize(duplicate.getSpawn1()));
                configuration.set(path + ".duplicates." + i + ".spawn2", LocationUtil.serialize(duplicate.getSpawn2()));
                configuration.set(path + ".duplicates." + i + ".max", LocationUtil.serialize(duplicate.getMax()));
                configuration.set(path + ".duplicates." + i + ".min", LocationUtil.serialize(duplicate.getMin()));
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

}
