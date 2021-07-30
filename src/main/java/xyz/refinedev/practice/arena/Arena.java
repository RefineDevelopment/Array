package xyz.refinedev.practice.arena;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.impl.SharedArena;
import xyz.refinedev.practice.arena.impl.StandaloneArena;
import xyz.refinedev.practice.arena.impl.TheBridgeArena;
import xyz.refinedev.practice.arena.meta.Rating;
import xyz.refinedev.practice.arena.meta.RatingType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.arena.meta.cuboid.Cuboid;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.location.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class Arena {

    @Getter private static final List<Arena> arenas = new ArrayList<>();
    @Getter @Setter public static boolean pasting = false;

    private List<String> kits = new ArrayList<>();

    protected final String name;
    protected Rating rating;
    protected String displayName;
    protected ItemStack displayIcon;
    protected Location spawn1, spawn2, min, max;
    protected boolean active, disablePearls;

    public Arena(String name) {
        this.name = name;
        this.rating = new Rating(this);
        this.displayName = CC.RED + name;
        this.displayIcon = new ItemStack(Material.PAPER);
    }

    public static void preload() {
        FileConfiguration configuration = Array.getInstance().getArenasConfig().getConfiguration();

        if (configuration.contains("arenas")) {
            //To see if the config is empty or not
            if (configuration.getConfigurationSection("arenas") == null) return;

            for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {

                String path = "arenas." + arenaName;
                ArenaType arenaType = ArenaType.valueOf(configuration.getString(path + ".type"));

                Arena arena;

                switch (arenaType) {
                    case STANDALONE: {
                        arena = new StandaloneArena(arenaName);
                        break;
                    }
                    case SHARED: {
                        arena = new SharedArena(arenaName);
                        break;
                    }
                    case THEBRIDGE: {
                        arena = new TheBridgeArena(arenaName);
                        break;
                    }
                    default: {
                        continue;
                    }
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

                    //If "redCuboid" location exist then init it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".redCuboid.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".redCuboid.location2"));
                    standaloneArena.setRedCuboid(new Cuboid(location1, location2));

                    //If "blueCuboid" location exist then init it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".blueCuboid.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".blueCuboid.location2"));
                    standaloneArena.setBlueCuboid(new Cuboid(location1, location2));

                    //If "bluePortal" location exist then init it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".bluePortal.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".bluePortal.location2"));
                    standaloneArena.setBluePortal(new Cuboid(location1, location2));

                    //If "redPortal" location exist then init it
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

                        arenas.add(duplicate);
                    }
                }
                arenas.add(arena);
            }
        }
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null && arena.getName().equalsIgnoreCase(name)) return arena;
        }

        return null;
    }

    public ItemStack getDisplayIcon() {
        return this.displayIcon.clone();
    }

    public static Arena getRandom(Kit kit) {
        List<Arena> _arenas = new ArrayList<>();

        for (Arena arena : arenas) {
            if (!arena.isSetup() || !arena.getKits().contains(kit.getName())) continue;

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

        return _arenas.get(Array.random.nextInt(_arenas.size()));
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
        if (spawn1 == null) return null;

        return spawn1.clone();
    }

    public Location getSpawn2() {
        if (spawn2 == null) return null;

        return spawn2.clone();
    }


    public void setActive(boolean active) {
        if (getType() != ArenaType.SHARED) this.active = active;
    }

    public void save() {
        //This are overrided in the types and are not use for Duplicate Arenas which is the default type of
        //arena in the main class
    }

    public void delete() {
        //This are overrided in the types and are not use for Duplicate Arenas which is the default type of
        //arena in the main class
    }
}
