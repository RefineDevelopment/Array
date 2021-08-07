package xyz.refinedev.practice.util.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

public class MenuConfig {
    
    private final FileConfiguration config;
    private final File configFile;

    public MenuConfig(String name, JavaPlugin plugin) {
        configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getString(String path) {
        return this.config.contains(path) ? ChatColor.translateAlternateColorCodes('&', this.config.getString(path)) : null;
    }

    public String getStringOrDefault(String path, String or) {
        String toReturn = this.getString(path);
        return toReturn == null ? or : toReturn;
    }

    public int getInteger(String path) {
        return this.config.contains(path) ? this.config.getInt(path) : 0;
    }

    public int getInteger(String path, int or) {
        int toReturn = this.getInteger(path);
        return this.config.contains(path) ? or : toReturn;
    }

    public void set(String path, Object value) {
        this.config.set(path, value);
    }

    public boolean getBoolean(String path) {
        return this.config.contains(path) && this.config.getBoolean(path);
    }

    public double getDouble(String path) {
        return this.config.contains(path) ? this.config.getDouble(path) : 0.0D;
    }

    public Object get(String path) {
        return this.config.contains(path) ? this.config.get(path) : null;
    }

    public List<String> getStringList(String path) {
        return this.config.contains(path) ? this.config.getStringList(path) : null;
    }

    public void reload(){
        try {
            config.load(configFile);
            config.save(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public File getFile() {
        return this.configFile;
    }
}
