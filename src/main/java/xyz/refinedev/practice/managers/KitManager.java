package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;

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
    private Kit teamFight;

    public void init() {
        ConfigurationSection configurationSection = config.getConfigurationSection("kits");
        if (configurationSection == null || configurationSection.getKeys(false).isEmpty()) return;

        if (plugin.getConfigHandler().isHCF_ENABLED()) {
            teamFight = new Kit("HCFTeamFight");
            teamFight.setDisplayIcon(new ItemBuilder(Material.BEACON).clearEnchantments().clearFlags().build());
        }

        for ( String kitName : configurationSection.getKeys(false) ) {
            Kit kit = new Kit(kitName);

            if (plugin.getConfigHandler().isHCF_ENABLED()) if (kit.getName().equals(this.teamFight.getName())) continue;

            this.load(kit);
            this.setupQueue(kit);
        }
        plugin.logger("&7Loaded &c" + kits.size() + " &7Kit(s)!");
    }

    /**
     * Load a {@link Kit} from the config
     * This method sets up the kit's icon,
     * {@link KitGameRules} and {@link KitInventory}
     *
     * @param kit The {@link Kit} being loaded
     */
    public void load(Kit kit) {
        String path = "kits." + kit.getName();
        kit.setDisplayName(CC.RED + kit.getName());
        kit.setEnabled(config.getBoolean(path + ".enabled"));

        if (config.contains(path + ".display-name")) {
            kit.setDisplayName(CC.translate(config.getString(path + ".display-name")));
        }

        kit.setKnockbackProfile(config.getString(path + ".knockback-profile"));

        if (config.getStringList(path + ".description") != null) {
            kit.getKitDescription().addAll(config.getStringList(path + ".description"));
        }

        kit.setDisplayIcon(new ItemBuilder(Material.valueOf(config.getString(path + ".icon.material")))
                .durability(config.getInteger(path + ".icon.durability"))
                .build());

        if (config.contains(path + ".loadout.armor")) {
            kit.getKitInventory().setArmor(InventoryUtil.deserializeInventory(config.getString(path + ".loadout.armor")));
        }

        if (config.contains(path + ".loadout.contents")) {
            kit.getKitInventory().setContents(InventoryUtil.deserializeInventory(config.getString(path + ".loadout.contents")));
        }

        if (config.contains(path + ".loadout.effects")) {
            kit.getKitInventory().setEffects(InventoryUtil.deserializeEffects(config.getString(path + ".loadout.effects")));
        }

        kit.getGameRules().setRanked(config.getBoolean(path + ".game-rules.ranked"));
        kit.getGameRules().setClan(config.getBoolean(path + ".game-rules.clan"));
        kit.getGameRules().setDisablePartyFFA(!config.getBoolean(path + ".game-rules.party-ffa"));
        kit.getGameRules().setDisablePartySplit(!config.getBoolean(path + ".game-rules.party-split"));
        kit.getGameRules().setEditable(config.getBoolean(path + ".game-rules.editable"));
        kit.getGameRules().setAntiFoodLoss(!config.getBoolean(path + ".game-rules.hunger"));
        kit.getGameRules().setNoItems(config.getBoolean(path + ".game-rules.noItems"));
        kit.getGameRules().setBuild(config.getBoolean(path + ".game-rules.build"));
        kit.getGameRules().setBridge(config.getBoolean(path + ".game-rules.bridge"));
        kit.getGameRules().setBoxing(config.getBoolean(path + ".game-rules.boxing"));
        kit.getGameRules().setBedwars(config.getBoolean(path + ".game-rules.bedwars"));
        kit.getGameRules().setSpleef(config.getBoolean(path + ".game-rules.spleef"));
        kit.getGameRules().setParkour(config.getBoolean(path + ".game-rules.parkour"));
        kit.getGameRules().setCombo(config.getBoolean(path + ".game-rules.combo"));
        kit.getGameRules().setBattleRush(config.getBoolean(path + ".game-rules.battle-rush"));
        kit.getGameRules().setStickSpawn(config.getBoolean(path + ".game-rules.stickspawn"));
        kit.getGameRules().setVoidSpawn(config.getBoolean(path + ".game-rules.voidspawn"));
        kit.getGameRules().setDisableFallDamage(!config.getBoolean(path + ".game-rules.fall-damage"));
        kit.getGameRules().setSumo(config.getBoolean(path + ".game-rules.sumo"));
        kit.getGameRules().setMlgRush(config.getBoolean(path + ".game-rules.mlgrush"));
        kit.getGameRules().setBoxuhc(config.getBoolean(path + ".game-rules.boxuhc"));
        kit.getGameRules().setTimed(config.getBoolean(path + ".game-rules.timed"));
        kit.getGameRules().setWaterKill(config.getBoolean(path + ".game-rules.water-kill"));
        kit.getGameRules().setLavaKill(config.getBoolean(path + ".game-rules.lava-kill"));
        kit.getGameRules().setRegen(config.getBoolean(path + ".game-rules.health-regen"));
        kit.getGameRules().setSpeed(config.getBoolean(path + ".game-rules.speed"));
        kit.getGameRules().setStrength(config.getBoolean(path + ".game-rules.strength"));
        kit.getGameRules().setShowHealth(config.getBoolean(path + ".game-rules.show-health"));
        kit.getGameRules().setBowHP(config.getBoolean(path + ".game-rules.bow-hp"));
        kit.getGameRules().setHitDelay(config.getInteger(path + ".game-rules.hit-delay"));

        if (config.getConfigurationSection(path + ".edit-rules.items") != null) {
            for ( String itemKey : config.getConfigurationSection(path + ".edit-rules.items").getKeys(false) ) {
                String pathKey = path + ".edit-rules.items." + itemKey;
                kit.getEditorItems().add(new ItemBuilder(Material.valueOf(config.getString(pathKey + ".material")))
                        .durability(config.getInteger(pathKey + ".durability"))
                        .amount(config.getInteger(pathKey + ".amount"))
                        .build());
            }
        }
    }

    /**
     * Save the {@link Kit} to config
     *
     * @param kit The {@link Kit} being saved
     */
    public void save(Kit kit) {
        String path = "kits." + kit.getName();
        
        config.set(path + ".enabled", kit.isEnabled());
        config.set(path + ".display-name", kit.getDisplayName());
        config.set(path + ".knockback-profile", kit.getKnockbackProfile());
        config.set(path + ".description", kit.getKitDescription());

        config.set(path + ".icon.material", kit.getDisplayIcon().getType().name());
        config.set(path + ".icon.durability", kit.getDisplayIcon().getDurability());

        config.set(path + ".loadout.armor", InventoryUtil.serializeInventory(kit.getKitInventory().getArmor()));
        config.set(path + ".loadout.contents", InventoryUtil.serializeInventory(kit.getKitInventory().getContents()));
        config.set(path + ".loadout.effects", InventoryUtil.serializeEffects(kit.getKitInventory().getEffects()));

        config.set(path + ".game-rules.ranked", kit.getGameRules().isRanked());
        config.set(path + ".game-rules.clan", kit.getGameRules().isClan());
        config.set(path + ".game-rules.party-ffa", !kit.getGameRules().isDisablePartyFFA());
        config.set(path + ".game-rules.party-split", !kit.getGameRules().isDisablePartySplit());
        config.set(path + ".game-rules.editable", kit.getGameRules().isEditable());
        config.set(path + ".game-rules.hunger", !kit.getGameRules().isAntiFoodLoss());
        config.set(path + ".game-rules.noitems", kit.getGameRules().isNoItems());
        config.set(path + ".game-rules.build", kit.getGameRules().isBuild());
        config.set(path + ".game-rules.bridge", kit.getGameRules().isBridge());
        config.set(path + ".game-rules.boxing", kit.getGameRules().isBoxing());
        config.set(path + ".game-rules.bedwars", kit.getGameRules().isBedwars());
        config.set(path + ".game-rules.battlerush", kit.getGameRules().isBattleRush());
        config.set(path + ".game-rules.spleef", kit.getGameRules().isSpleef());
        config.set(path + ".game-rules.parkour", kit.getGameRules().isParkour());
        config.set(path + ".game-rules.fall-damage", !kit.getGameRules().isDisableFallDamage());
        config.set(path + ".game-rules.stickspawn", kit.getGameRules().isStickSpawn());
        config.set(path + ".game-rules.voidspawn", kit.getGameRules().isVoidSpawn());
        config.set(path + ".game-rules.mlgrush", kit.getGameRules().isMlgRush());
        config.set(path + ".game-rules.combo", kit.getGameRules().isCombo());
        config.set(path + ".game-rules.sumo", kit.getGameRules().isSumo());
        config.set(path + ".game-rules.boxuhc", kit.getGameRules().isBoxuhc());
        config.set(path + ".game-rules.timed", kit.getGameRules().isTimed());
        config.set(path + ".game-rules.water-kill", kit.getGameRules().isWaterKill());
        config.set(path + ".game-rules.lava-kill", kit.getGameRules().isLavaKill());
        config.set(path + ".game-rules.health-regeneration", kit.getGameRules().isRegen());
        config.set(path + ".game-rules.speed", kit.getGameRules().isSpeed());
        config.set(path + ".game-rules.strength", kit.getGameRules().isStrength());
        config.set(path + ".game-rules.show-health", kit.getGameRules().isShowHealth());
        config.set(path + ".game-rules.hit-delay", kit.getGameRules().getHitDelay());
        config.set(path + ".game-rules.bow-hp", kit.getGameRules().isBowHP());

        config.save();
    }

    /**
     * Delete a kit from the config and server
     *
     * @param kit The kit being deleted
     */
    public void delete(Kit kit) {
        kits.remove(kit);

        if (kit.isEnabled()) plugin.getQueueManager().getQueues().remove(kit.getUnrankedQueue().getUniqueId());
        if (kit.getGameRules().isRanked()) plugin.getQueueManager().getQueues().remove(kit.getRankedQueue().getUniqueId());
        if (kit.getGameRules().isClan()) plugin.getQueueManager().getQueues().remove(kit.getClanQueue().getUniqueId());

        config.set("kits." + kit.getName(), null);
        config.save();
    }

    /**
     * Setup the {@link Kit}'s queue
     *
     * @param kit {@link Kit} whose queue is being setup
     */
    public void setupQueue(Kit kit) {
        if (!kit.isEnabled()) return;

        Queue unranked = new Queue(plugin, kit, QueueType.UNRANKED);
        kit.setUnrankedQueue(unranked);

        if (kit.getGameRules().isRanked()) {
            Queue ranked = new Queue(plugin, kit, QueueType.RANKED);
            kit.setRankedQueue(ranked);
        }
        if (kit.getGameRules().isClan()) {
            Queue clan = new Queue(plugin, kit, QueueType.CLAN);
            kit.setRankedQueue(clan);
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
