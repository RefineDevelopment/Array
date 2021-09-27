package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    }

    public void load(Kit kit) {

    }

    public void save(Kit kit) {

    }

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

    public void shutdownQueue(Kit kit) {

    }

    public Kit getByName(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }
}
