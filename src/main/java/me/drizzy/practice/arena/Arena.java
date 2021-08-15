package me.drizzy.practice.arena;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.impl.SharedArena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.arena.cuboid.Cuboid;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.location.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Arena {

    @Getter private static final List<Arena> arenas = new ArrayList<>();
    private List<String> kits = new ArrayList<>();
    public ItemStack displayIcon;

    @Getter @Setter public static boolean pasting = false;

    protected final String name;
    protected String displayName;
    protected Location spawn1;
    protected Location spawn2;
    protected Location min;
    protected Location max;
    protected boolean active;
    protected boolean disablePearls;

    public Arena(String name) {
        this.name = name;
        this.displayName = CC.RED + name;
        this.displayIcon = new ItemStack(Material.PAPER);
    }

    public static void preload() {
        FileConfiguration configuration = Array.getInstance().getArenasConfig().getConfiguration();

        if (configuration.contains("arenas")) {
            if (configuration.getConfigurationSection("arenas") == null) return;
            for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName;
                ArenaType arenaType = ArenaType.valueOf(configuration.getString(path + ".type"));

                Arena arena;
                if (arenaType == ArenaType.STANDALONE) {
                    arena = new StandaloneArena(arenaName);
                } else if (arenaType == ArenaType.SHARED) {
                    arena = new SharedArena(arenaName);
                } else if (arenaType == ArenaType.THEBRIDGE) {
                    arena = new TheBridgeArena(arenaName);
                } else {
                    continue;
                }

                if (configuration.contains(path + ".display-name")) {
                    arena.setDisplayName(CC.translate(configuration.getString(path + ".display-name")));
                }

                if (configuration.contains(path + ".icon-material")) {
                    arena.setDisplayIcon(new ItemBuilder(Material.valueOf(configuration.getString(path + ".icon.material")))
                            .durability(configuration.getInt(path + ".icon.durability"))
                            .build());
                } else {
                    arena.setDisplayIcon(new ItemBuilder(Material.PAPER).durability(0).build());
                }

                if (configuration.contains(path + ".disable-pearls")) {
                    arena.setDisablePearls(configuration.getBoolean(path + ".disable-pearls"));
                } else {
                    arena.setDisablePearls(false);
                }

                if (configuration.contains(path + ".spawn1")) {
                    arena.setSpawn1(LocationUtil.deserialize(configuration.getString(path + ".spawn1")));
                }

                if (configuration.contains(path + ".spawn2")) {
                    arena.setSpawn2(LocationUtil.deserialize(configuration.getString(path + ".spawn2")));
                }

                if (configuration.contains(path + ".max")) {
                    arena.setMax(LocationUtil.deserialize(configuration.getString(path + ".max")));
                }

                if (configuration.contains(path + ".min")) {
                    arena.setMin(LocationUtil.deserialize(configuration.getString(path + ".min")));
                }


                if (arena instanceof TheBridgeArena && configuration.contains(path + ".redCuboid") && configuration.contains(path + ".blueCuboid")) {
                    Location location1;
                    Location location2;
                    //Declare the arena as type TheBridge
                    TheBridgeArena standaloneArena = (TheBridgeArena) arena;

                    //If "redCuboid" location exist then load it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".redCuboid.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".redCuboid.location2"));
                    standaloneArena.setRedCuboid(new Cuboid(location1, location2));

                    //If "blueCuboid" location exist then load it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".blueCuboid.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".blueCuboid.location2"));
                    standaloneArena.setBlueCuboid(new Cuboid(location1, location2));

                    //If "bluePortal" location exist then load it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".bluePortal.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".bluePortal.location2"));
                    standaloneArena.setBluePortal(new Cuboid(location1, location2));

                    //If "redPortal" location exist then load it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".redPortal.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".redPortal.location2"));
                    standaloneArena.setRedPortal(new Cuboid(location1, location2));

                }

                if (configuration.contains(path + ".kits")) {
                    for (String kitName : configuration.getStringList(path + ".kits")) {
                        arena.getKits().add(kitName);
                    }
                }

                if (arena instanceof StandaloneArena && configuration.contains(path + ".duplicates")) {
                    for (String duplicateId : configuration.getConfigurationSection(path + ".duplicates").getKeys(false)) {
                        Location spawn1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn1"));
                        Location spawn2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn2"));
                        Location max = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".max"));
                        Location min = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".min"));


                        Arena duplicate = new Arena(arenaName);
                        duplicate.setDisplayName(arena.getDisplayName());
                        duplicate.setSpawn1(spawn1);
                        duplicate.setSpawn2(spawn2);
                        duplicate.setMax(max);
                        duplicate.setMin(min);
                        duplicate.setKits(arena.getKits());

                        ((StandaloneArena) arena).getDuplicates().add(duplicate);

                        Arena.getArenas().add(duplicate);
                    }
                }

                Arena.getArenas().add(arena);
            }
        }

        Array.logger("&aLoaded " + Arena.getArenas().size() + " arenas!");
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null &&
                    arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public ItemStack getDisplayIcon() {
        return this.displayIcon.clone();
    }

    public static Arena getRandom(Kit kit) {
        List<Arena> _arenas = new ArrayList<>();

        for (Arena arena : arenas) {
            if (!arena.isSetup()) continue;

            if (!arena.getKits().contains(kit.getName())) continue;

            if ((arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE || arena.getType() == ArenaType.SHARED) && kit.getGameRules().isBridge()) continue;

            if (!arena.isActive() && (arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE)) {
                _arenas.add(arena);
            } else if (!kit.getGameRules().isBuild() && !kit.getGameRules().isBridge() && arena.getType() == ArenaType.SHARED) {
                _arenas.add(arena);
            } else if (kit.getGameRules().isBridge() && arena.getType() == ArenaType.THEBRIDGE) {
                _arenas.add(arena);
            }
        }

        if (_arenas.isEmpty()) {
            return null;
        }

        return _arenas.get(ThreadLocalRandom.current().nextInt(_arenas.size()));
    }

    public ArenaType getType() {
        return ArenaType.DUPLICATE;
    }

    public boolean isSetup() {
        return spawn1 != null && spawn2 != null && max != null && min != null;
    }

    public int getMaxBuildHeight() {
        int highest = (int) (Math.max(spawn1.getY(), spawn2.getY()));
        return highest + 5;
    }

    public Location getSpawn1() {
        if (spawn1 == null) {
            return null;
        }

        return spawn1.clone();
    }

    public Location getSpawn2() {
        if (spawn2 == null) {
            return null;
        }

        return spawn2.clone();
    }


    public void setActive(boolean active) {
        if (getType() != ArenaType.SHARED) {
            this.active = active;
        }
    }

    public void save() {
    }

    public void delete() {
    }

}
