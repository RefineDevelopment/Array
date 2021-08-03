package xyz.refinedev.practice.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/3/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class KillEffectManager {

    private final List<KillEffect> killEffects = new LinkedList<>();

    private final Array plugin;
    private final BasicConfigurationFile config;

    public void init() {
        ConfigurationSection section = config.getConfiguration().getConfigurationSection("KILL_EFFECTS.");
        if (section == null || section.getKeys(false) == null) return;

    }

    public KillEffect getByUUID(UUID uuid) {
        return killEffects.stream().filter(killEffect -> killEffect.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }
}
