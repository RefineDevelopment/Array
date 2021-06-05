package me.drizzy.practice.managers;

import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.brawl.Brawl;
import me.drizzy.practice.brawl.arena.BrawlArena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.location.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/26/2021
 * Project: Array
 */

/*public class BrawlManager {

    @Getter public static final List<BrawlArena> arenas = new ArrayList<>();

    private final Array plugin = Array.getInstance();
    private final BasicConfigurationFile config = plugin.getBrawlConfig();

    public void load() {
        FileConfiguration config = plugin.getBrawlConfig().getConfiguration();

        if (config.contains("ARENAS")) {
            if (config.getConfigurationSection("ARENAS") == null) return;

            for (String arenaName : config.getConfigurationSection("ARENAS").getKeys(false)) {
                String key = "ARENAS." + arenaName + ".";

                BrawlArena arena = new BrawlArena(arenaName);
                arena.setEnabled(config.getBoolean(key + "ENABLED"));
                arena.setSpawn1(LocationUtil.deserialize(config.getString(key + "SPAWN1")));
                arena.setSpawn2(LocationUtil.deserialize(config.getString(key + "SPAWN2")));

                arenas.add(arena);
            }
        }

        if (config.contains("BRAWLS")) {
            if (config.getConfigurationSection("BRAWLS") == null) return;

            for ( String brawlName : config.getConfigurationSection("BRAWLS").getKeys(false) ) {
                String key = "BRAWLS." + brawlName + ".";

                Brawl brawl = new Brawl(brawlName);
                brawl.setDisplayName(config.getString(key + "DISPLAY_NAME"));
                brawl.setKit(Kit.getByName(config.getString(key + "KIT")));
                brawl.setArena(BrawlArena.getByName(config.getString(key + "ARENA")));
                brawl.setEnabled(config.getBoolean(key + "ENABLED"));
            }
        }
        this.config.save();
    }

    public void addPlayer(Brawl brawl, Player player) {

    }

    public void removePlayer(Brawl brawl, Player player) {

    }
}*/
