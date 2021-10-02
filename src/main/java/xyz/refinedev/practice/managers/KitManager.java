package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/17/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class KitManager {

    private final Array plugin;
    private final BasicConfigurationFile config;

    private final List<Kit> kits = new ArrayList<>();

    public void init() {
        ConfigurationSection configurationSection = config.getConfigurationSection("kits");
        if (configurationSection == null || configurationSection.getKeys(false).isEmpty()) return;

        for ( String kitName : configurationSection.getKeys(false) ) {
            Kit kit = new Kit(kitName);

            this.load(kit);
            this.setupQueue(kit);
        }
    }

    public void load(Kit kit) {

    }

    public void save(Kit kit) {

    }

    /**
     * Setup the {@link Kit}'s queue
     *
     * @param kit {@link Kit} whose queue is being setup
     */
    public void setupQueue(Kit kit) {
        if (!kit.isEnabled()) return;
        kit.setUnrankedQueue(new Queue(kit, QueueType.UNRANKED));

        if (kit.getGameRules().isRanked()) {
            kit.setRankedQueue(new Queue(kit, QueueType.RANKED));
        }
        if (kit.getGameRules().isClan()) {
            kit.setClanQueue(new Queue(kit, QueueType.CLAN));
        }
    }

    /**
     * Get a {@link Kit} by its name
     *
     * @param name {@link String} name of the kit
     * @return {@link Kit} Queried kit
     */
    public Kit getByName(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }
}
