package me.array.ArrayPractice.arena.impl;

import me.array.ArrayPractice.arena.*;
import me.array.ArrayPractice.*;
import me.array.ArrayPractice.util.external.*;
import java.io.*;
import org.bukkit.configuration.file.*;
import java.util.*;

public class StandaloneArena extends Arena
{
    public StandaloneArena(final String name) {
        super(name);
    }
    
    @Override
    public ArenaType getType() {
        return ArenaType.STANDALONE;
    }
    
    @Override
    public void save() {
        System.out.println("STANDALONE ARENA SAVE");
        String path = "arenas." + getName();

        FileConfiguration configuration = Array.get().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());
        configuration.set(path + ".spawnA", LocationUtil.serialize(spawnA));
        configuration.set(path + ".spawnB", LocationUtil.serialize(spawnB));
        configuration.set(path + ".kits", getKits());

        try {
            configuration.save(Array.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        super.delete();

        FileConfiguration configuration = Array.get().getArenasConfig().getConfiguration();
        configuration.set("arenas." + getName(), null);

        try {
            configuration.save(Array.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
