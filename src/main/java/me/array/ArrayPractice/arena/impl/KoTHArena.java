package me.array.ArrayPractice.arena.impl;

import me.array.ArrayPractice.arena.*;
import me.array.ArrayPractice.*;
import me.array.ArrayPractice.util.external.*;
import java.io.*;
import org.bukkit.configuration.file.*;

public class KoTHArena extends Arena
{
    public KoTHArena(final String name) {
        super(name);
    }
    
    @Override
    public ArenaType getType() {
        return ArenaType.KOTH;
    }
    
    @Override
    public void save() {
        final String path = "arenas." + this.getName();
        final FileConfiguration configuration = Array.get().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", this.getType().name());
        if (this.spawn1 != null) {
            configuration.set(path + ".spawn1", LocationUtil.serialize(this.spawn1));
        }
        if (this.spawn2 != null) {
            configuration.set(path + ".spawn2", LocationUtil.serialize(this.spawn2));
        }
        if (this.point1 != null) {
            configuration.set(path + ".point1", LocationUtil.serialize(this.point1));
        }
        if (this.point2 != null) {
            configuration.set(path + ".point2", LocationUtil.serialize(this.point2));
        }
        configuration.set(path + ".kits", this.getKits());
        try {
            configuration.save(Array.get().getArenasConfig().getFile());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void delete() {
        final FileConfiguration configuration = Array.get().getArenasConfig().getConfiguration();
        configuration.set("arenas." + this.getName(), null);
        try {
            configuration.save(Array.get().getArenasConfig().getFile());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
