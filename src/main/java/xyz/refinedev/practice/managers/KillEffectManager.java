package xyz.refinedev.practice.managers;

import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;

import java.util.Arrays;
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

@Getter
public class KillEffectManager {

    private final List<KillEffect> killEffects = new LinkedList<>();
    private final BasicConfigurationFile config;

    public KillEffectManager(Array plugin) {
        this.config = plugin.getKillEffectsConfig();
    }

    public void init() {
        killEffects.add(getDefault());
        ConfigurationSection section = config.getConfigurationSection("KILL_EFFECTS.");
        if (section == null || section.getKeys(false) == null) return;

        for ( String key : section.getKeys(false) ) {
            String path = "KILL_EFFECTS." + key + ".";
            UUID uuid = UUID.fromString(config.getStringOrDefault(path + "UUID", UUID.randomUUID().toString()));
            KillEffect killEffect = new KillEffect(uuid);
            killEffect.setDisplayName(config.getString(path + "NAME"));
            if (!config.getString(path + "EFFECT").equalsIgnoreCase("NONE")) {
                killEffect.setEffect(Effect.valueOf(config.getString(path + "EFFECT")));
            }
            killEffect.setPermissionEnabled(config.getBoolean(path + "PERMISSION.ENABLED"));
            killEffect.setPermission(config.getStringOrDefault(path + "PERMISSION.STRING", "NONE"));
            killEffect.setData(config.getInteger(path + "DATA"));
            killEffect.setLightning(config.getBoolean(path + "LIGHTNING"));
            killEffect.setDropsClear(config.getBoolean(path + "CLEAR_ITEMS"));
            killEffect.getDescription().addAll(config.getStringList(path + "DESCRIPTION"));

            ConfigurationSection soundSection = config.getConfigurationSection(path + "SOUND");
            if (soundSection == null || soundSection.getKeys(false) == null) return;

            for ( String sound_key : soundSection.getKeys(false) ) {
                String sound_path = path + "SOUND." + sound_key + ".";
                KillEffectSound killEffectSound = new KillEffectSound();
                killEffectSound.setSound(Sound.valueOf(config.getString(sound_path + "TYPE")));
                if (config.contains(sound_path + "PITCH")) killEffectSound.setPitch(Float.parseFloat(config.getString(sound_path + "PITCH")));
                killEffect.getKillEffectSounds().add(killEffectSound);
            }
            this.killEffects.add(killEffect);
        }
    }

    public KillEffect getByUUID(UUID uuid) {
        return killEffects.stream().filter(killEffect -> killEffect.getUniqueId().equals(uuid)).findFirst().orElse(getDefault());
    }

    public KillEffect getDefault() {
        KillEffect killEffect = new KillEffect(UUID.randomUUID());
        killEffect.setDisplayName("&aDefault");
        killEffect.setLightning(true);
        killEffect.setDropsClear(true);
        killEffect.getDescription().addAll(Arrays.asList("This is the default kill effect"));

        return killEffect;
    }
}
