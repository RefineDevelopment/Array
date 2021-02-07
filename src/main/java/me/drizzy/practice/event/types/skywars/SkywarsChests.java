package me.drizzy.practice.event.types.skywars;

import me.drizzy.practice.Array;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class SkywarsChests {

    private UUID uuid;
    private Location location;
    private Chest block;
    private ChestType type;

    public static Map<Location, SkywarsChests> chests = new HashMap<>();

    private static YamlConfiguration config = Array.getInstance().getChestsConfig().getConfiguration();

    public SkywarsChests(UUID uuid, Location location, ChestType type) {
        this.uuid = uuid;
        this.location = location;
        this.block = (Chest) location.getBlock().getState();
        this.type = type;
        chests.put(location, this);
    }

    public void save() {
        config.set(uuid.toString(), location.getWorld().getName() + "@" + location.getX() + "@" + location.getY() + "@" + location.getZ() + "#" + type.name());
        try {
            config.save(Array.getInstance().getChestsConfig().getFile());
        } catch (Exception ignored) {}
    }

    public static void preload() {
        for (String uuidString : config.getKeys(false)) {
            String[] args = config.getString(uuidString).split("#");
            String[] locArgs = args[0].split("@");
            new SkywarsChests(UUID.fromString(uuidString),
                    new Location(Bukkit.getWorld(locArgs[0]), Double.parseDouble(locArgs[1]), Double.parseDouble(locArgs[2]), Double.parseDouble(locArgs[3])),
                    ChestType.valueOf(args[1]));
        }
    }

    public static SkywarsChests getFromLocation(Location location) {
        return chests.getOrDefault(location, null);
    }

}
