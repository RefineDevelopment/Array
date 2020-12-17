package me.array.ArrayPractice.arena;

import org.bukkit.*;
import me.array.ArrayPractice.util.*;
import me.array.ArrayPractice.*;
import me.array.ArrayPractice.arena.impl.*;
import me.array.ArrayPractice.util.external.*;
import org.bukkit.configuration.file.*;

import java.util.*;
import me.array.ArrayPractice.kit.*;
import java.util.concurrent.*;

public class Arena {
    private static List<Arena> arenas;
    protected String name;
    protected String type;
    protected Location spawn1;
    protected Location spawn2;
    protected Location point1;
    protected Location point2;
    private KothPoint point;
    protected boolean active;
    private List<String> kits;

    public Arena(final String name) {
        this.kits=new ArrayList<String>();
        this.name=name;
    }

    public ArenaType getType() {
        final FileConfiguration configuration=Array.get().getArenasConfig().getConfiguration();
        for ( final String arenaName : configuration.getConfigurationSection("arenas").getKeys(false) ) {
            final String path="arenas." + arenaName;
            final ArenaType arenaType=ArenaType.valueOf(configuration.getString(path + ".type"));
            if (arenaType == ArenaType.DUPLICATE) {
                return ArenaType.DUPLICATE;
            }
            if (arenaType == ArenaType.SHARED) {
                return ArenaType.SHARED;
            }
            if (arenaType == ArenaType.STANDALONE) {
                return ArenaType.STANDALONE;
            }
        }
        return ArenaType.SHARED;
    }

    public void setType(String type) {
        FileConfiguration configuration = Array.get().getArenasConfig().getConfiguration();
        for ( final String arenaName : configuration.getConfigurationSection("arenas").getKeys(false) ) {
            String path = "arenas." + arenaName;
            Array.get().getArenasConfig().getConfiguration().set(path + ".type", type.toUpperCase());
        }

    }
    public boolean isSetup() {
        return this.spawn1 != null && this.spawn2 != null;
    }
    
    public int getMaxBuildHeight() {
        final int highest = (int)((this.spawn1.getY() >= this.spawn2.getY()) ? this.spawn1.getY() : this.spawn2.getY());
        return highest + 5;
    }
    
    public Location getSpawn1() {
        if (this.spawn1 == null) {
            return null;
        }
        return this.spawn1.clone();
    }
    
    public Location getSpawn2() {
        if (this.spawn2 == null) {
            return null;
        }
        return this.spawn2.clone();
    }
    
    public Location getPoint1() {
        if (this.point1 == null) {
            return null;
        }
        return this.point1.clone();
    }
    
    public Location getPoint2() {
        if (this.point2 == null) {
            return null;
        }
        return this.point2.clone();
    }
    
    public void setActive(final boolean active) {
        if (this.getType() != ArenaType.SHARED && this.getType() != ArenaType.KOTH) {
            this.active = active;
        }
    }
    
    public void save() {
    }

    public void delete() {
        arenas.remove(this);
    }
    
    public static void init() {
        final FileConfiguration configuration = (FileConfiguration)Array.get().getArenasConfig().getConfiguration();
        if (configuration.contains("arenas")) {
            if (configuration.getConfigurationSection("arenas") == null) {
                return;
            }
            for (final String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
                final String path = "arenas." + arenaName;
                final ArenaType arenaType = ArenaType.valueOf(configuration.getString(path + ".type"));
                Arena arena;
                if (arenaType == ArenaType.STANDALONE) {
                    arena = new StandaloneArena(arenaName);
                }
                else if (arenaType == ArenaType.SHARED) {
                    arena = new SharedArena(arenaName);
                }
                else {
                    if (arenaType != ArenaType.KOTH) {
                        continue;
                    }
                    arena = new KoTHArena(arenaName);
                }
                if (configuration.contains(path + ".spawn1")) {
                    arena.setSpawn1(LocationUtil.deserialize(configuration.getString(path + ".spawn1")));
                }
                if (configuration.contains(path + ".spawn2")) {
                    arena.setSpawn2(LocationUtil.deserialize(configuration.getString(path + ".spawn2")));
                }
                if (configuration.contains(path + ".point1") && configuration.contains(path + ".point2")) {
                    arena.setPoint1(LocationUtil.deserialize(configuration.getString(path + ".point1")));
                    arena.setPoint2(LocationUtil.deserialize(configuration.getString(path + ".point2")));
                    arena.setPoint(new KothPoint(arena.point1, arena.point2));
                }
                if (configuration.contains(path + ".kits")) {
                    for (final String kitName : configuration.getStringList(path + ".kits")) {
                        arena.getKits().add(kitName);
                    }
                }
                if (arena instanceof StandaloneArena && configuration.contains(path + ".duplicates")) {
                    for (final String duplicateId : configuration.getConfigurationSection(path + ".duplicates").getKeys(false)) {
                        final Location spawn1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn1"));
                        final Location spawn2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn2"));
                        final Arena duplicate = new Arena(arenaName);
                        duplicate.setSpawn1(spawn1);
                        duplicate.setSpawn2(spawn2);
                        duplicate.setKits(arena.getKits());
                        ((StandaloneArena)arena).getDuplicates().add(duplicate);
                        getArenas().add(duplicate);
                    }
                }
                getArenas().add(arena);
            }
        }
        Array.get().getLogger().info("Loaded " + getArenas().size() + " arenas");
    }
    
    public static ArenaType getTypeByName(final String name) {
        for (final ArenaType arena : ArenaType.values()) {
            if (arena.toString().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }
    
    public static Arena getByName(final String name) {
        for (final Arena arena : Arena.arenas) {
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null && arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }
    
    public static Arena getRandom(final Kit kit) {
        final List<Arena> _arenas = new ArrayList<Arena>();
        for (final Arena arena : Arena.arenas) {
            if (!arena.isSetup()) {
                continue;
            }
            if (!arena.getKits().contains(kit.getName())) {
                continue;
            }
            if (arena.getType() == ArenaType.KOTH) {
                _arenas.add(arena);
            }
            if (!arena.isActive() && (arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE)) {
                _arenas.add(arena);
            }
            else {
                if (kit.getGameRules().isBuild() || arena.getType() != ArenaType.SHARED) {
                    continue;
                }
                _arenas.add(arena);
            }
        }
        if (_arenas.isEmpty()) {
            return null;
        }
        return _arenas.get(ThreadLocalRandom.current().nextInt(_arenas.size()));
    }
    
    public static List<Arena> getArenas() {
        return Arena.arenas;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setSpawn1(final Location spawn1) {
        this.spawn1 = spawn1;
    }
    
    public void setSpawn2(final Location spawn2) {
        this.spawn2 = spawn2;
    }
    
    public void setPoint1(final Location point1) {
        this.point1 = point1;
    }
    
    public void setPoint2(final Location point2) {
        this.point2 = point2;
    }
    
    public KothPoint getPoint() {
        return this.point;
    }
    
    public void setPoint(final KothPoint point) {
        this.point = point;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public List<String> getKits() {
        return this.kits;
    }
    
    public void setKits(final List<String> kits) {
        this.kits = kits;
    }
    
    static {
        Arena.arenas = new ArrayList<Arena>();
    }
}
