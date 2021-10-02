package xyz.refinedev.practice.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;

import java.util.ArrayList;
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

    private final Array plugin;
    private final MongoCollection<Document> collection;
    private final BasicConfigurationFile config;

    private final List<KillEffect> killEffects = new ArrayList<>();

    public KillEffectManager(Array plugin) {
        this.plugin = plugin;
        this.collection = plugin.getMongoManager().getKillEffects();
        this.config = plugin.getKillEffectsConfig();
    }

    /**
     * Initiate and load are Kill Effects
     */
    public void init() {
        plugin.submitToThread(() -> {
            for ( Document document : collection.find() ) {
                if (document == null) {
                    this.importConfig();
                    return;
                }

                KillEffect killEffect = Array.GSON.fromJson(document.getString("killEffect"), KillEffect.class);
                synchronized (this.killEffects) {
                    this.killEffects.add(killEffect);
                }
            }
        });
    }

    /**
     * Import {@link KillEffect} from config
     */
    public void importConfig() {
        //if there are any then clear them
        this.killEffects.clear();

        ConfigurationSection section = config.getConfigurationSection("KILL_EFFECTS.");
        if (section == null || section.getKeys(false) == null) {
            this.createDefault();
            return;
        }

        for ( String key : section.getKeys(false) ) {
            String path = "KILL_EFFECTS." + key + ".";

            UUID uuid = UUID.fromString(config.getString(path + "UUID"));
            KillEffect killEffect = new KillEffect(uuid, key);

            killEffect.setDisplayName(config.getString(path + "NAME"));
            killEffect.setPermissionEnabled(config.getBoolean(path + "PERMISSION.ENABLED"));
            killEffect.setPermission(config.getStringOrDefault(path + "PERMISSION.STRING", "NONE"));
            if (!config.getString(path + "EFFECT.TYPE").equalsIgnoreCase("NONE")) {
                killEffect.setEffect(Effect.valueOf(config.getString(path + "EFFECT.TYPE")));
            }
            killEffect.setData(config.getInteger(path + "EFFECT.DATA"));
            killEffect.setDefaultEffect(config.getBoolean(path + "DEFAULT"));
            killEffect.setAnimateDeath(config.getBoolean(path + "ANIMATE_DEATH"));
            killEffect.setLightning(config.getBoolean(path + "LIGHTNING"));
            killEffect.setDropsClear(config.getBoolean(path + "CLEAR_ITEMS"));
            killEffect.getDescription().addAll(config.getStringList(path + "DESCRIPTION"));

            Material material = Material.valueOf(config.getString(path + "ICON.MATERIAL"));
            ItemBuilder itemBuilder = new ItemBuilder(material);

            itemBuilder.name(killEffect.getName());
            if (config.getInteger("ICON.DATA") != 0) {
                itemBuilder.durability(config.getInteger(path + "ICON.DATA"));
            }

            killEffect.setItemStack(itemBuilder.build());

            ConfigurationSection soundSection = config.getConfigurationSection(path + "SOUND");
            if (soundSection == null || soundSection.getKeys(false) == null) return;

            for ( String sound_key : soundSection.getKeys(false) ) {
                String sound_path = path + "SOUND." + sound_key + ".";
                KillEffectSound killEffectSound = new KillEffectSound();
                killEffectSound.setKey(sound_key);
                killEffectSound.setSound(Sound.valueOf(config.getString(sound_path + "TYPE")));
                if (config.contains(sound_path + "PITCH")) {
                    killEffectSound.setPitch(Float.parseFloat(config.getString(sound_path + "PITCH")));
                }
                killEffect.getKillEffectSounds().add(killEffectSound);
            }
            this.killEffects.add(killEffect);
        }

        if (killEffects.stream().noneMatch(KillEffect::isDefaultEffect) || killEffects.isEmpty()) {
            this.createDefault();
        }
    }

    /**
     * Export all {@link KillEffect} to config
     */
    public void exportConfig() {
        for ( KillEffect killEffect : killEffects ) {
            if (killEffect == null) return;

            String path = "KILL_EFFECTS." + killEffect.getName() + ".";

            config.set(path + "NAME", killEffect.getDisplayName());
            config.set(path + "PERMISSION.ENABLED", killEffect.isPermissionEnabled());
            config.set(path + "PERMISSION.STRING", killEffect.getPermission());
            config.set(path + "EFFECT.TYPE", killEffect.getEffect() == null ? "None" : killEffect.getEffect().name());
            config.set(path + "EFFECT.DATA", killEffect.getData());
            config.set(path + "DEFAULT", killEffect.isDefaultEffect());
            config.set(path + "ANIMATE_DEATH", killEffect.isAnimateDeath());
            config.set(path + "LIGHTNING", killEffect.isLightning());
            config.set(path + "CLEAR_ITEMS", killEffect.isDropsClear());
            config.set(path + "ICON.MATERIAL", killEffect.getItemStack().getType().name());
            config.set(path + "ICON.DATA", killEffect.getItemStack().getDurability());

            for ( KillEffectSound sound : killEffect.getKillEffectSounds() ) {
                String soundPath = path + "SOUND." + sound.getKey();

                config.set(soundPath + "TYPE", sound.getSound().name());
                config.set(soundPath + "PITCH", sound.getPitch());
            }
            config.set(path + "DESCRIPTION", killEffect.getDescription());
        }
        config.save();
    }

    /**
     * Save a {@link KillEffect} to mongo
     *
     * @param killEffect {@link KillEffect} to save
     */
    public void save(KillEffect killEffect) {
        String serialized = Array.GSON.toJson(killEffect);
        Document document = new Document();

        document.put("killEffect", serialized);
        document.put("_id", killEffect.getUniqueId());

        plugin.submitToThread(() -> collection.replaceOne(Filters.eq("_id", killEffect.getUniqueId().toString()), document, new ReplaceOptions().upsert(true)));
    }

    /**
     * Get a {@link KillEffect} by its {@link UUID}
     *
     * @param uuid {@link UUID} of the kill effect
     * @return {@link KillEffect}
     */
    public KillEffect getByUUID(UUID uuid) {
        return killEffects.stream().filter(killEffect -> killEffect.getUniqueId().equals(uuid)).findFirst().orElse(getDefault());
    }

    /**
     * Get a {@link KillEffect} by its name
     *
     * @param name Name of the kill effect
     * @return {@link KillEffect}
     */
    public KillEffect getByName(String name) {
        return killEffects.stream().filter(killEffect -> killEffect.getName().equals(name)).findFirst().orElse(getDefault());
    }

    /**
     * Get the default kill effect
     *
     * @return {@link KillEffect}
     */
    public KillEffect getDefault() {
        return killEffects.stream().filter(KillEffect::isDefaultEffect).findAny().orElse(createDefault());
    }

    /**
     * Create the default {@link KillEffect}
     * and return it
     *
     * @return {@link KillEffect}
     */
    public KillEffect createDefault() {
        KillEffect killEffect = new KillEffect(UUID.randomUUID(), "&aDefault");
        killEffect.setLightning(true);
        killEffect.setDropsClear(true);
        killEffect.setDefaultEffect(true);
        killEffect.setItemStack(new ItemStack(Material.PAPER));

        this.killEffects.add(killEffect);
        return killEffect;
    }
}
