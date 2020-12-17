package me.array.ArrayPractice.arena.impl;

import me.array.ArrayPractice.arena.*;
import me.array.ArrayPractice.*;
import me.array.ArrayPractice.util.external.*;
import java.io.*;
import org.bukkit.configuration.file.*;
import java.util.*;

public class StandaloneArena extends Arena
{
    private List<Arena> duplicates;
    
    public StandaloneArena(final String name) {
        super(name);
        this.duplicates = new ArrayList<Arena>();
    }
    
    @Override
    public ArenaType getType() {
        return ArenaType.STANDALONE;
    }
    
    @Override
    public void save() {
        final String path = "arenas." + this.getName();
        final FileConfiguration configuration = (FileConfiguration)Array.get().getArenasConfig().getConfiguration();
        configuration.set(path, (Object)null);
        configuration.set(path + ".type", (Object)this.getType().name());
        if (this.spawn1 != null) {
            configuration.set(path + ".spawn1", (Object)LocationUtil.serialize(this.spawn1));
        }
        if (this.spawn2 != null) {
            configuration.set(path + ".spawn2", (Object)LocationUtil.serialize(this.spawn2));
        }
        configuration.set(path + ".kits", (Object)this.getKits());
        if (!this.duplicates.isEmpty()) {
            int i = 0;
            for (final Arena duplicate : this.duplicates) {
                ++i;
                configuration.set(path + ".duplicates." + i + ".spawn1", (Object)LocationUtil.serialize(duplicate.getSpawn1()));
                configuration.set(path + ".duplicates." + i + ".spawn2", (Object)LocationUtil.serialize(duplicate.getSpawn2()));
            }
        }
        try {
            configuration.save(Array.get().getArenasConfig().getFile());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void delete() {
        final FileConfiguration configuration = (FileConfiguration)Array.get().getArenasConfig().getConfiguration();
        configuration.set("arenas." + this.getName(), (Object)null);
        try {
            configuration.save(Array.get().getArenasConfig().getFile());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Arena> getDuplicates() {
        return this.duplicates;
    }
}
