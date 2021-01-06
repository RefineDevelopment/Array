package me.array.ArrayPractice.arena.impl;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter
@Setter
public class KoTHArena extends Arena {

    public KoTHArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.KOTH;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        FileConfiguration configuration = Practice.get().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());

        if (spawn1 != null) {
            configuration.set(path + ".spawn1", LocationUtil.serialize(spawn1));
        }

        if (spawn2 != null) {
            configuration.set(path + ".spawn2", LocationUtil.serialize(spawn2));
        }

        if (point1 != null) {
            configuration.set(path + ".point1", LocationUtil.serialize(point1));
        }

        if (point2 != null) {
            configuration.set(path + ".point2", LocationUtil.serialize(point2));
        }

        configuration.set(path + ".kits", getKits());

        try {
            configuration.save(Practice.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        FileConfiguration configuration = Practice.get().getArenasConfig().getConfiguration();
        configuration.set("arenas." + getName(), null);

        try {
            configuration.save(Practice.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
