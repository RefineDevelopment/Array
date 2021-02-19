package me.drizzy.practice.kit;

import com.mongodb.client.model.Sorts;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import org.bukkit.metadata.MetadataValue;
import me.drizzy.practice.util.InventoryUtil;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.external.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Kit {

    @Getter
    private static final List<Kit> kits = new ArrayList<>();

    public static final String currentKit = "ArrayCurrentKit";
    @Getter
    private final String name;
    @Getter
    private final KitLoadout kitLoadout = new KitLoadout();
    @Getter
    private final KitEditRules editRules = new KitEditRules();
    @Getter
    private final KitGameRules gameRules = new KitGameRules();
    @Getter
    @Setter
    private boolean enabled;
    @Getter
    @Setter
    private String knockbackProfile;
    @Setter
    private ItemStack displayIcon;
    @Getter
    private final List<KitLeaderboards> rankedEloLeaderboards = new ArrayList<>();

    public Kit(String name) {
        this.name = name;
        this.displayIcon = new ItemStack(Material.DIAMOND_SWORD);
        this.knockbackProfile = "default";
    }
    public void delete() {
        kits.remove(this);
        Array.getInstance().getKitsConfig().getConfiguration().set("kits." + getName(), null);
        try {
            Array.getInstance().getKitsConfig().getConfiguration().save(Array.getInstance().getKitsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void preload() {
        FileConfiguration config = Array.getInstance().getKitsConfig().getConfiguration();
        FileConfiguration mainConfig = Array.getInstance().getMainConfig().getConfiguration();
        for (String key : config.getConfigurationSection("kits").getKeys(false)) {
            String path = "kits." + key;

            Kit kit = new Kit(key);
            kit.setEnabled(config.getBoolean(path + ".enabled"));
            kit.setKnockbackProfile(config.getString(path + ".knockback-profile"));

            kit.setDisplayIcon(new ItemBuilder(Material.valueOf(config.getString(path + ".icon.material")))
                    .durability(config.getInt(path + ".icon.durability"))
                    .build());

            if (config.contains(path + ".loadout.armor")) {
                kit.getKitLoadout().setArmor(InventoryUtil.deserializeInventory(config.getString(path + ".loadout.armor")));
            }

            if (config.contains(path + ".loadout.contents")) {
                kit.getKitLoadout().setContents(InventoryUtil.deserializeInventory(config.getString(path + ".loadout.contents")));
            }

            if (config.contains(path + ".loadout.effects")) {
                kit.getKitLoadout().setEffects(InventoryUtil.deserializeEffects(config.getString(path + ".loadout.effects")));
            }

            kit.getGameRules().setRanked(config.getBoolean(path + ".game-rules.ranked"));
            kit.getGameRules().setPartyffa(config.getBoolean(path + ".game-rules.partyffa"));
            kit.getGameRules().setPartysplit(config.getBoolean(path + ".game-rules.partysplit"));
            kit.getGameRules().setEditable(config.getBoolean(path + ".game-rules.editable"));
            kit.getGameRules().setAntifoodloss(config.getBoolean(path + ".game-rules.antifoodloss"));
            kit.getGameRules().setFfacenter(config.getBoolean(path + ".game-rules.ffacenter"));
            kit.getGameRules().setNoitems(config.getBoolean(path + ".game-rules.noitems"));
            kit.getGameRules().setBuild(config.getBoolean(path + ".game-rules.build"));
            kit.getGameRules().setSpleef(config.getBoolean(path + ".game-rules.spleef"));
            kit.getGameRules().setParkour(config.getBoolean(path + ".game-rules.parkour"));
            kit.getGameRules().setCombo(config.getBoolean(path + ".game-rules.combo"));
            kit.getGameRules().setStickspawn(config.getBoolean(path + ".game-rules.stickspawn"));
            kit.getGameRules().setVoidspawn(config.getBoolean(path + ".game-rules.voidspawn"));
            kit.getGameRules().setNetheruhc(config.contains(path + ".game-rules.netheruhc"));
            kit.getGameRules().setSumo(config.getBoolean(path + ".game-rules.sumo"));
            kit.getGameRules().setBoxuhc(config.getBoolean(path + ".game-rules.boxuhc"));
            kit.getGameRules().setTimed(config.getBoolean(path + ".game-rules.timed"));
            kit.getGameRules().setWaterkill(config.getBoolean(path + ".game-rules.water-kill"));
            kit.getGameRules().setLavakill(config.getBoolean(path + ".game-rules.lava-kill"));
            kit.getGameRules().setHealthRegeneration(config.getBoolean(path + ".game-rules.health-regeneration"));
            kit.getGameRules().setInfinitespeed(config.getBoolean(path + ".game-rules.infinite-speed"));
            kit.getGameRules().setInfinitestrength(config.getBoolean(path + ".game-rules.infinite-strength"));
            kit.getGameRules().setShowHealth(config.getBoolean(path + ".game-rules.show-health"));
            kit.getGameRules().setBowhp(config.getBoolean(path + ".game-rules.bow-hp"));
            kit.getGameRules().setHitDelay(config.getInt(path + ".game-rules.hit-delay"));
            kit.getEditRules().setAllowPotionFill(config.getBoolean(".edit-rules.allow-potion-fill"));
            if (config.getConfigurationSection(path + ".edit-rules.items") != null) {
                for (String itemKey : config.getConfigurationSection(path + ".edit-rules.items").getKeys(false)) {
                    kit.getEditRules().getEditorItems().add(
                            new ItemBuilder(Material.valueOf(config.getString(path + ".edit-rules.items." + itemKey + ".material")))
                                    .durability(config.getInt(path + ".edit-rules.items." + itemKey + ".durability"))
                                    .amount(config.getInt(path + ".edit-rules.items." + itemKey + ".amount"))
                                    .build());
                }
            }

            kits.add(kit);
        }

        kits.forEach(kit -> {
            if (kit.isEnabled()) {
                new Queue(kit, QueueType.UNRANKED);
                if (kit.getGameRules().isRanked()) {
                    new Queue(kit, QueueType.RANKED);
                }
            }
        });

        Kit.getKits().forEach(Kit::updateKitLeaderboards);
    }

    public static Kit getByName(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        return null;
    }

    public void giveKitMeta(Metadatable metadatable) {
        metadatable.setMetadata(currentKit, new FixedMetadataValue(Array.getInstance(), this));
    }

    public static Kit getCurrentKit(Metadatable metadatable){
        if(metadatable.hasMetadata(currentKit)) {
            MetadataValue m = Array.getInstance().getMetadata(metadatable, currentKit);
            if(m != null && m.value() != null && m.value() instanceof Kit) {
                return (Kit) m.value();
            }
        }
        return null;
    }


    public ItemStack getDisplayIcon() {
        return this.displayIcon.clone();
    }

    public void save() {
        String path = "kits." + name;

        BasicConfigurationFile configFile = Array.getInstance().getKitsConfig();
        configFile.getConfiguration().set(path + ".enabled", enabled);
        configFile.getConfiguration().set(path + ".knockback-profile", knockbackProfile);
        configFile.getConfiguration().set(path + ".icon.material", displayIcon.getType().name());
        configFile.getConfiguration().set(path + ".icon.durability", displayIcon.getDurability());
        configFile.getConfiguration().set(path + ".loadout.armor", InventoryUtil.serializeInventory(kitLoadout.getArmor()));
        configFile.getConfiguration().set(path + ".loadout.contents", InventoryUtil.serializeInventory(kitLoadout.getContents()));
        configFile.getConfiguration().set(path + ".loadout.effects", InventoryUtil.serializeEffects(kitLoadout.getEffects()));
        configFile.getConfiguration().set(path + ".game-rules.ranked", gameRules.isRanked());
        configFile.getConfiguration().set(path + ".game-rules.partyffa", gameRules.isPartyffa());
        configFile.getConfiguration().set(path + ".game-rules.partysplit", gameRules.isPartysplit());
        configFile.getConfiguration().set(path + ".game-rules.editable", gameRules.isEditable());
        configFile.getConfiguration().set(path + ".game-rules.antifoodloss", gameRules.isAntifoodloss());
        configFile.getConfiguration().set(path + ".game-rules.ffacenter", gameRules.isFfacenter());
        configFile.getConfiguration().set(path + ".game-rules.noitems", gameRules.isNoitems());
        configFile.getConfiguration().set(path + ".game-rules.build", gameRules.isBuild());
        configFile.getConfiguration().set(path + ".game-rules.spleef", gameRules.isSpleef());
        configFile.getConfiguration().set(path + ".game-rules.parkour", gameRules.isParkour());
        configFile.getConfiguration().set(path + ".game-rules.netheruhc", gameRules.isNetheruhc());
        configFile.getConfiguration().set(path + ".game-rules.stickspawn", gameRules.isStickspawn());
        configFile.getConfiguration().set(path + ".game-rules.voidspawn", gameRules.isVoidspawn());
        configFile.getConfiguration().set(path + ".game-rules.combo", gameRules.isCombo());
        configFile.getConfiguration().set(path + ".game-rules.sumo", gameRules.isSumo());
        configFile.getConfiguration().set(path + ".game-rules.boxuhc", gameRules.isBoxuhc());
        configFile.getConfiguration().set(path + ".game-rules.timed", gameRules.isTimed());
        configFile.getConfiguration().set(path + ".game-rules.water-kill", gameRules.isWaterkill());
        configFile.getConfiguration().set(path + ".game-rules.lava-kill", gameRules.isLavakill());
        configFile.getConfiguration().set(path + ".game-rules.health-regeneration", gameRules.isHealthRegeneration());
        configFile.getConfiguration().set(path + ".game-rules.infinite-speed", gameRules.isInfinitespeed());
        configFile.getConfiguration().set(path + ".game-rules.infinite-strength", gameRules.isInfinitestrength());
        configFile.getConfiguration().set(path + ".game-rules.show-health", gameRules.isShowHealth());
        configFile.getConfiguration().set(path + ".game-rules.hit-delay", gameRules.getHitDelay());
        configFile.getConfiguration().set(path + ".game-rules.bow-hp", gameRules.isBowhp());
        configFile.getConfiguration().set(path + ".edit-rules.allow-potion-fill", editRules.isAllowPotionFill());

        try {
            configFile.getConfiguration().save(configFile.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateKitLeaderboards() {
        if (!this.getRankedEloLeaderboards().isEmpty()) this.getRankedEloLeaderboards().clear();
        for (Document document : Profile.getAllProfiles().find().sort(Sorts.descending("kitStatistics." + getName() + ".elo")).limit(10).into(new ArrayList<>())) {
            Document kitStatistics = (Document) document.get("kitStatistics");
            if (kitStatistics.containsKey(getName())) {
                Document kitDocument = (Document) kitStatistics.get(getName());
                KitLeaderboards kitLeaderboards = new KitLeaderboards();
                kitLeaderboards.setName((String) document.get("name"));
                kitLeaderboards.setElo((Integer) kitDocument.get("elo"));
                this.getRankedEloLeaderboards().add(kitLeaderboards);
            }
        }
    }

}