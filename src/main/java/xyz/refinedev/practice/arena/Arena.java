package xyz.refinedev.practice.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.rating.Rating;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.chat.CC;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

@Getter @Setter
public abstract class Arena {

    private final Array plugin;
    private List<Kit> kits = new ArrayList<>();

    private final String name;
    private String displayName;
    private Location spawn1, spawn2, min, max;

    private ArenaType type;
    private Rating rating = new Rating(this, 0, 0, 0,0, 0);
    private ItemStack displayIcon = new ItemStack(Material.PAPER);

    private int fallDeathHeight;
    private boolean active, duplicate, disablePearls;

    public Arena(Array plugin, String name, ArenaType arenaType) {
        this.plugin = plugin;
        this.name = name;
        this.type = arenaType;
        this.displayName = CC.RED + name;

        plugin.getArenaManager().getArenas().add(this);
    }

    /*public void preload() {
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
                        arena = new StandaloneArena(plugin, arenaName);
                        break;
                    }
                    case SHARED: {
                        arena = new SharedArena(plugin, arenaName);
                        break;
                    }
                    case BRIDGE: {
                        arena = new TheBridgeArena(plugin, arenaName);
                        break;
                    }
                    default: {
                        continue;
                    }
                }

                arena.setActive(false);

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

                if (configuration.contains(path + ".fall-death-height")) {
                    arena.setFallDeathHeight(configuration.getInt(path + ".fall-death-height", 25));
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


                        StandaloneArena duplicate = new StandaloneArena(plugin, arenaName);
                        duplicate.setDisplayName(arena.getDisplayName());
                        duplicate.setSpawn1(spawn1);
                        duplicate.setSpawn2(spawn2);
                        duplicate.setMax(max);
                        duplicate.setMin(min);
                        duplicate.setKits(arena.getKits());

                        duplicate.setDuplicate(true);

                        ((StandaloneArena) arena).getDuplicates().add(duplicate);

                        arenas.add(duplicate);
                    }
                } else if (arena instanceof TheBridgeArena && configuration.contains(path + "duplicates")) {
                    for (String duplicateId : configuration.getConfigurationSection(path + ".duplicates").getKeys(false)) {
                        Location spawn1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn1"));
                        Location spawn2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn2"));
                        Location max = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".max"));
                        Location min = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".min"));


                        TheBridgeArena duplicate = new TheBridgeArena(plugin, arenaName);
                        duplicate.setDisplayName(arena.getDisplayName());
                        duplicate.setSpawn1(spawn1);
                        duplicate.setSpawn2(spawn2);
                        duplicate.setMax(max);
                        duplicate.setMin(min);
                        duplicate.setKits(arena.getKits());

                        Location location1;
                        Location location2;

                        //If "redCuboid" location exist then init it
                        location1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".redCuboid.location1"));
                        location2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".redCuboid.location2"));
                        duplicate.setRedCuboid(new Cuboid(location1, location2));

                        //If "blueCuboid" location exist then init it
                        location1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".blueCuboid.location1"));
                        location2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".blueCuboid.location2"));
                        duplicate.setBlueCuboid(new Cuboid(location1, location2));

                        //If "bluePortal" location exist then init it
                        location1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".bluePortal.location1"));
                        location2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".bluePortal.location2"));
                        duplicate.setBluePortal(new Cuboid(location1, location2));

                        //If "redPortal" location exist then init it
                        location1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".redPortal.location1"));
                        location2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".redPortal.location2"));
                        duplicate.setRedPortal(new Cuboid(location1, location2));

                        duplicate.setDuplicate(true);

                        ((TheBridgeArena) arena).getDuplicates().add(duplicate);

                        arenas.add(duplicate);
                    }
                }
                arenas.add(arena);
            }
        }
    }*/

    public int getMaxBuildHeight() {
        int highest = (int) (Math.max(spawn1.getY(), spawn2.getY()));
        return highest + 5;
    }

    public int getFallDeathHeight() {
        return this.getSpawn1().getBlockY() - this.fallDeathHeight;
    }

    public void setActive(boolean active) {
        if (this.getType() != ArenaType.SHARED) this.active = active;
    }

    public boolean isStandalone() {
        return this.type == ArenaType.STANDALONE;
    }

    public boolean isBridge() {
        return this.type == ArenaType.BRIDGE;
    }

    public boolean isShared() {
        return this.type == ArenaType.SHARED;
    }

    public abstract void save();

    public abstract boolean isSetup();
}
