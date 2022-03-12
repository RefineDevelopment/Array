package xyz.refinedev.practice.hook.hologram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.hook.hologram.impl.ClanHologram;
import xyz.refinedev.practice.hook.hologram.impl.GlobalHologram;
import xyz.refinedev.practice.hook.hologram.impl.KitHologram;
import xyz.refinedev.practice.hook.hologram.impl.SwitchHologram;
import xyz.refinedev.practice.hook.hologram.task.HologramUpdateTask;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class HologramHandler {

    private final Array plugin;
    private final BasicConfigurationFile config;
    private final List<PracticeHologram> holograms = new ArrayList<>();

    /**
     * Load and initiate holograms
     */
    public final void init() {
        ConfigurationSection section = config.getConfigurationSection("HOLOGRAMS");
        if (section == null || section.getKeys(false).isEmpty()) return;

        for ( String key : section.getKeys(false) ) {
            HologramType type;

            try {
                type = HologramType.valueOf(section.getString(key + ".TYPE"));
            } catch (Exception e) {
                plugin.consoleLog("&cInvalid Type in hologram " + section.getString(key) + ", skipping!");
                continue;
            }

            PracticeHologram hologram;
            ConfigurationSection hologramSection = section.getConfigurationSection(key);

            switch (type) {
                case KIT: {
                    Kit kit = plugin.getKitManager().getByName(section.getString(key + ".KIT"));
                    hologram = new KitHologram(plugin, kit);
                    this.load(hologram, hologramSection, HologramType.KIT);
                    this.holograms.add(hologram);
                    break;
                }
                case CLAN: {
                    hologram = new ClanHologram(plugin);
                    this.load(hologram, hologramSection, HologramType.CLAN);
                    this.holograms.add(hologram);
                    break;
                }
                case SWITCH: {
                    hologram = new SwitchHologram(plugin);
                    this.load(hologram, hologramSection, HologramType.SWITCH);
                    this.holograms.add(hologram);
                    break;
                }
                default: {
                    hologram = new GlobalHologram(plugin);
                    this.load(hologram, hologramSection, HologramType.GLOBAL);
                    this.holograms.add(hologram);
                    break;
                }
            }
        }
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new HologramUpdateTask(plugin), 20L, 20L);
    }

    /**
     * Load a Hologram's meta from config
     *
     * @param hologram {@link PracticeHologram} hologram
     * @param section  {@link ConfigurationSection} the config section of hologram
     * @param type     {@link HologramType} type of hologram
     */
    public final void load(PracticeHologram hologram, ConfigurationSection section, HologramType type) {
        HologramMeta meta = new HologramMeta();

        meta.setLocation(LocationUtil.deserialize(section.getString("LOCATION")));
        meta.setName(section.getName());
        meta.setWorld(meta.getLocation().getWorld());
        meta.setType(type);

        hologram.setMeta(meta);
        hologram.spawn();
    }

    /**
     * Save a hologram to the config
     *
     * @param hologram {@link PracticeHologram} hologram
     */
    public final void save(PracticeHologram hologram) {
        HologramMeta meta = hologram.getMeta();
        String path = "HOLOGRAMS." + meta.getName() + ".";

        config.set(path + "LOCATION", LocationUtil.serialize(meta.getLocation()));
        config.set(path + "TYPE", meta.getType().name());

        config.save();
    }

}
