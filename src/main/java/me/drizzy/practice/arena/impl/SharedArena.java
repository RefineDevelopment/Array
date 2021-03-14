package me.drizzy.practice.arena.impl;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter
@Setter
public class SharedArena extends Arena {

    public SharedArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.SHARED;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        FileConfiguration configuration = Array.getInstance().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());
        configuration.set(path + ".icon.material", displayIcon.getType().name());
        configuration.set(path + ".icon.durability", displayIcon.getDurability());
        configuration.set(path + ".disable-pearls", disablePearls);
        if (spawn1 != null) {
            configuration.set(path + ".spawn1", LocationUtil.serialize(spawn1));
        }

        if (spawn2 != null) {
            configuration.set(path + ".spawn2", LocationUtil.serialize(spawn2));
        }

        configuration.set(path + ".kits", getKits());

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
