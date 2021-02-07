package me.drizzy.practice.util.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class BasicConfigurationFile extends AbstractConfigurationFile {
    private final File file;
    private final YamlConfiguration configuration;

    public BasicConfigurationFile(JavaPlugin plugin, String name, boolean overwrite) {
        super(plugin, name);
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        plugin.saveResource(name + ".yml", overwrite);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public BasicConfigurationFile(JavaPlugin plugin, String name) {
        this(plugin, name, false);
    }

    public String getString(String path) {
        return this.configuration.contains(path) ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : null;
    }

    public String getStringOrDefault(String path, String or) {
        String toReturn = this.getString(path);
        return toReturn == null ? or : toReturn;
    }

    public int getInteger(String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    public double getDouble(String path) {
        return this.configuration.contains(path) ? this.configuration.getDouble(path) : 0.0D;
    }

    public Object get(String path) {
        return this.configuration.contains(path) ? this.configuration.get(path) : null;
    }

    public List<String> getStringList(String path) {
        return this.configuration.contains(path) ? this.configuration.getStringList(path) : null;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }
}
