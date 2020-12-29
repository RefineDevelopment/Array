package me.array.ArrayPractice.arena;

import lombok.Setter;
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
    protected Location spawnA;
    protected Location spawnB;
    protected Location point1;
    protected Location point2;
    private KothPoint point;
    protected boolean active;
    private List<String> kits;

    public Arena(String name) {
        this.kits = new ArrayList<>();
        this.name = name;
    }

    public ArenaType getType() {
        return ArenaType.DUPLICATE;
    }

    public boolean isSetup() {
        return this.spawnA != null && this.spawnB != null;
    }

    public int getMaxBuildHeight() {
        int highest = (int) (Math.max(this.spawnA.getY(), this.spawnB.getY()));
        return highest + 5;
    }

    public Location getSpawnA() {
        if (spawnA == null) {
            return null;
        }

        return spawnA.clone();
    }

    public Location getSpawnB() {
        if (spawnB == null) {
            return null;
        }

        return spawnB.clone();
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
        final FileConfiguration configuration=Array.get().getArenasConfig().getConfiguration();
        if (configuration.contains("arenas")) {
            if (configuration.getConfigurationSection("arenas") == null) {
                return;
            }
            for ( final String arenaName : configuration.getConfigurationSection("arenas").getKeys(false) ) {
                final String path="arenas." + arenaName;
                final ArenaType arenaType=ArenaType.valueOf(configuration.getString(path + ".type"));
                Arena arena;

                if (arenaType == ArenaType.STANDALONE) {
                    arena=new StandaloneArena(arenaName);
                } else if (arenaType == ArenaType.SHARED) {
                    arena=new SharedArena(arenaName);
                } else {
                    continue;
                }

                if (configuration.contains(path + ".spawnA")) {
                    arena.spawnA.add(LocationUtil.deserialize(configuration.getString(path + ".spawnA")));
                }

                if (configuration.contains(path + ".spawnB")) {
                    arena.spawnA.add(LocationUtil.deserialize(configuration.getString(path + ".spawnB")));
                }

                if (configuration.contains(path + ".kits")) {
                    for ( String kitName : configuration.getStringList(path + ".kits") ) {
                        arena.getKits().add(kitName);
                    }

                }
                getArenas().add(arena);
            }
            Array.get().getLogger().info("Loaded " + getArenas().size() + " arenas");
        }
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
        final List<Arena> _arenas = new ArrayList<>();
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
            if (!arena.isActive() && (arena.getType() == ArenaType.STANDALONE)) {
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
    
    public void setSpawnA(final Location spawnA) {
        this.spawnA = spawnA;
    }
    
    public void setSpawnB(final Location spawnB) {
        this.spawnA = spawnB;
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
        Arena.arenas = new ArrayList<>();
    }
}
