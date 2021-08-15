package me.drizzy.practice.util.config;

import java.io.IOException;
import java.util.Collections;

import lombok.Getter;
import me.drizzy.practice.Array;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Map;

@Getter
public class LanguageConfigurationFile extends AbstractConfigurationFile {

    private static final Lang  DEFAULT_LOCALE = Lang.ENGLISH;;
    private final Map<Lang, YamlConfiguration> configurations;
    
    public LanguageConfigurationFile(final JavaPlugin plugin, final String name, final boolean overwrite) {
        super(plugin, name);
        this.configurations = new HashMap<>();
        for (Lang locale : Lang.values()) {
            final File file = new File(plugin.getDataFolder(), name + "_" + locale.getAbbreviation() + ".yml");
            final String path = name + "_" + locale.getAbbreviation() + ".yml";
            if (plugin.getResource(path) != null) {
                plugin.saveResource(path, overwrite);
                this.configurations.put(locale, YamlConfiguration.loadConfiguration(file));
            }
        }
    }
    
    public LanguageConfigurationFile(final JavaPlugin plugin, final String name) {
        this(plugin, name, false);
    }
    
    public List<String> replace(final List<String> list, final int position, final Object argument) {
        final List<String> toReturn = new ArrayList<>();
        for (final String string : list) {
            toReturn.add(string.replace("{" + position + "}", argument.toString()));
        }
        return toReturn;
    }
    
    public List<String> replace(final List<String> list, final int position, final Object... arguments) {
        return this.replace(list, 0, position, arguments);
    }
    
    public List<String> replace(final List<String> list, final int index, final int position, final Object... arguments) {
        final List<String> toReturn = new ArrayList<>();
        for (final String string : list) {
            for (int i = 0; i < arguments.length; ++i) {
                toReturn.add(string.replace("{" + position + "}", arguments[index + i].toString()));
            }
        }
        return toReturn;
    }
    
    public List<String> getStringListWithArgumentsOrRemove(final String path, final Lang locale, final Object... arguments) {
        final List<String> toReturn = new ArrayList<>();for (String string : this.getStringList(path, locale)) {
            for (int i = 0; i < arguments.length; ++i) {
                if (string.contains("{" + i + "}")) {
                    final Object object = arguments[i];
                    if (object == null) {
                        continue;
                    }
                    if (object instanceof List) {
                        for (final Object obj : (List)object) {
                            if (obj instanceof String) {
                                toReturn.add((String)obj);
                            }
                        }
                        continue;
                    }
                    string = string.replace("{" + i + "}", object.toString());
                }
            }
            toReturn.add(string);
        }
        return toReturn;
    }
    
    public int indexOf(final List<String> list, final int position) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).contains("{" + position + "}")) {
                return i;
            }
        }
        return -1;
    }
    
    public String getString(final String path, final Lang locale) {
        if (!this.configurations.containsKey(locale)) {
            return (locale == LanguageConfigurationFile.DEFAULT_LOCALE) ? null : this.getString(path, LanguageConfigurationFile.DEFAULT_LOCALE);
        }
        final YamlConfiguration configuration = this.configurations.get(locale);
        if (configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', configuration.getString(path));
        }
        return null;
    }
    
    public String getString(final String path, final Lang locale, final Object... arguments) {
        String toReturn = this.getString(path, locale);
        if (toReturn != null) {
            for (int i = 0; i < arguments.length; ++i) {
                toReturn = toReturn.replace("{" + i + "}", arguments[i].toString());
            }
            return toReturn;
        }
        return null;
    }
    
    @Override
    public String getString(final String path) {
        return this.getString(path, LanguageConfigurationFile.DEFAULT_LOCALE);
    }
    
    public String getStringOrDefault(final String path, final String or, final Lang locale) {
        final String toReturn = this.getString(path, locale);
        if (toReturn == null) {
            return or;
        }
        return toReturn;
    }
    
    @Override
    public String getStringOrDefault(final String path, final String or) {
        return this.getStringOrDefault(path, or, LanguageConfigurationFile.DEFAULT_LOCALE);
    }

    @Override
    public int getInteger(final String path) {
        throw new UnsupportedOperationException("");
    }

    @Deprecated
    @Override
    public double getDouble(final String path) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Object get(final String path) {
        return get(path, Lang.ENGLISH);
    }

    public Object get(final String path, Lang locale) {
        if (!this.configurations.containsKey(locale)) {
            return (locale == LanguageConfigurationFile.DEFAULT_LOCALE) ? null :
                this.get(path, LanguageConfigurationFile.DEFAULT_LOCALE);
        }
        final YamlConfiguration configuration = this.configurations.get(locale);
        if (configuration.contains(path)) {
            return configuration.get(path);
        }
        return null;
    }

    public List<String> getStringList(final String path, final Lang locale, final Object... arguments) {
        final List<String> toReturn = new ArrayList<>();
        for (String line : this.getStringList(path, locale)) {
            for (int i = 0; i < arguments.length; ++i) {
                final Object object = arguments[i];
                if (object instanceof List && line.contains("{" + i + "}")) {
                    for (final Object obj : (List)object) {
                        if (obj instanceof String) {
                            toReturn.add(line.replace("{" + i + "}", "") + obj);
                        }
                    }
                    continue;
                }
                line = line.replace("{" + i + "}", arguments[i].toString());
            }
            toReturn.add(line);
        }
        return toReturn;
    }
    
    public List<String> getStringList(final String path, final Lang locale) {
        if (!this.configurations.containsKey(locale)) {
            return (locale == LanguageConfigurationFile.DEFAULT_LOCALE) ? null : this.getStringList(path, LanguageConfigurationFile.DEFAULT_LOCALE);
        }
        final YamlConfiguration configuration = this.configurations.get(locale);
        if (configuration.contains(path)) {
            final List<String> toReturn = new ArrayList<>();
            for (final String string : configuration.getStringList(path)) {
                toReturn.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return toReturn;
        }
        return Collections.emptyList();
    }


    public void reload() {
        getConfigurations().forEach((type, configuration) -> {
            final File file = new File(Array.getInstance().getDataFolder(),
                "lang" + "_" + type.getAbbreviation() + ".yml");
            try {
                configuration.load(file);
                configuration.save(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public List<String> getStringList(final String path) {
        return this.getStringList(path, LanguageConfigurationFile.DEFAULT_LOCALE);
    }
}
