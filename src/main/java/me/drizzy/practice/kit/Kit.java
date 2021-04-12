package me.drizzy.practice.kit;

import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.kiteditor.KitEditRules;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.statistics.LeaderboardsAdapter;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.InventoryUtil;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.external.ItemBuilder;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Kit {

    @Getter private static final List<Kit> kits = new ArrayList<>();
    private final String name;
    private final KitInventory kitInventory= new KitInventory();
    private final KitEditRules editRules = new KitEditRules();
    private final KitGameRules gameRules = new KitGameRules();
    @Setter private Queue unrankedQueue;
    @Setter private Queue rankedQueue;
    @Setter private boolean enabled;
    @Setter private String knockbackProfile;
    @Setter private ItemStack displayIcon;
    @Setter private String displayName;
    private final List<LeaderboardsAdapter> rankedEloLeaderboards = new ArrayList<>();
    private final List<LeaderboardsAdapter> winLeaderboards = new ArrayList<>();
    private final List<LeaderboardsAdapter> killsLeaderboards = new ArrayList<>();

    public Kit(String name) {
        this.name = name;
        this.displayName = CC.AQUA + name;
        this.displayIcon = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.knockbackProfile = "default";
    }

    public void delete() {
        kits.remove(this);
        Queue.getQueues().remove(rankedQueue);
        Queue.getQueues().remove(unrankedQueue);
        Array.getInstance().getKitsConfig().getConfiguration().set("kits." + getName(), null);
        try {
            Array.getInstance().getKitsConfig().getConfiguration().save(Array.getInstance().getKitsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void preload() {
        FileConfiguration config = Array.getInstance().getKitsConfig().getConfiguration();
        for (String key : config.getConfigurationSection("kits").getKeys(false)) {
            String path = "kits." + key;

            Kit kit = new Kit(key);
            kit.setDisplayName(CC.AQUA + kit.getName());
            kit.setEnabled(config.getBoolean(path + ".enabled"));
            if (config.contains(path + ".display-name")) {
                kit.setDisplayName(CC.translate(config.getString(path + ".display-name")));
            }
            kit.setKnockbackProfile(config.getString(path + ".knockback-profile"));

            kit.setDisplayIcon(new ItemBuilder(Material.valueOf(config.getString(path + ".icon.material")))
                    .durability(config.getInt(path + ".icon.durability"))
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
            kit.getGameRules().setDisablePartyFFA(config.getBoolean(path + ".game-rules.disablePartyFFA"));
            kit.getGameRules().setDisablePartySplit(config.getBoolean(path + ".game-rules.disablePartySplit"));
            kit.getGameRules().setEditable(config.getBoolean(path + ".game-rules.editable"));
            kit.getGameRules().setAntiFoodLoss(config.getBoolean(path + ".game-rules.antiFoodLoss"));
            kit.getGameRules().setNoItems(config.getBoolean(path + ".game-rules.noItems"));
            kit.getGameRules().setBuild(config.getBoolean(path + ".game-rules.build"));
            kit.getGameRules().setBridge(config.getBoolean(path + ".game-rules.bridge"));
            kit.getGameRules().setSpleef(config.getBoolean(path + ".game-rules.spleef"));
            kit.getGameRules().setParkour(config.getBoolean(path + ".game-rules.parkour"));
            kit.getGameRules().setCombo(config.getBoolean(path + ".game-rules.combo"));
            kit.getGameRules().setStickSpawn(config.getBoolean(path + ".game-rules.stickSpawn"));
            kit.getGameRules().setVoidSpawn(config.getBoolean(path + ".game-rules.voidSpawn"));
            kit.getGameRules().setDisableFallDamage(config.getBoolean(path + ".game-rules.disable-fall-damage"));
            kit.getGameRules().setSumo(config.getBoolean(path + ".game-rules.sumo"));
            kit.getGameRules().setBedwars(config.getBoolean(path + ".game-rules.bedwars"));
            kit.getGameRules().setMlgRush(config.getBoolean(path + ".game-rules.mlgRush"));
            kit.getGameRules().setBoxUHC(config.getBoolean(path + ".game-rules.boxUHC"));
            kit.getGameRules().setTimed(config.getBoolean(path + ".game-rules.timed"));
            kit.getGameRules().setWaterKill(config.getBoolean(path + ".game-rules.water-kill"));
            kit.getGameRules().setLavaKill(config.getBoolean(path + ".game-rules.lava-kill"));
            kit.getGameRules().setHealthRegeneration(config.getBoolean(path + ".game-rules.health-regeneration"));
            kit.getGameRules().setInfiniteSpeed(config.getBoolean(path + ".game-rules.infinite-speed"));
            kit.getGameRules().setInfiniteStrength(config.getBoolean(path + ".game-rules.infinite-strength"));
            kit.getGameRules().setShowHealth(config.getBoolean(path + ".game-rules.show-health"));
            kit.getGameRules().setBowHP(config.getBoolean(path + ".game-rules.bow-hp"));
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
                kit.setUnrankedQueue(new Queue(kit, QueueType.UNRANKED));
                if (kit.getGameRules().isRanked()) {
                    kit.setRankedQueue(new Queue(kit, QueueType.RANKED));
                }
            }
        });

        try {
            Kit.getKits().forEach(Kit::updateKitLeaderboards);
        } catch (Exception e) {
            Array.logger(CC.CHAT_BAR);
            Array.logger("            &4&lMongo Internal Error");
            Array.logger("      &cKit Leaderboards could not be loaded!");
            Array.logger("       &cPlease check your mongo and try again.");
            Array.logger("             &4&lDisabling Array");
            Array.logger(CC.CHAT_BAR);
            Bukkit.getPluginManager().disablePlugin(Array.getInstance());
        }
    }

    public static Kit getByName(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
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
        configFile.getConfiguration().set(path + ".display-name", displayName);
        configFile.getConfiguration().set(path + ".knockback-profile", knockbackProfile);
        configFile.getConfiguration().set(path + ".icon.material", displayIcon.getType().name());
        configFile.getConfiguration().set(path + ".icon.durability", displayIcon.getDurability());
        configFile.getConfiguration().set(path + ".loadout.armor", InventoryUtil.serializeInventory(kitInventory.getArmor()));
        configFile.getConfiguration().set(path + ".loadout.contents", InventoryUtil.serializeInventory(kitInventory.getContents()));
        configFile.getConfiguration().set(path + ".loadout.effects", InventoryUtil.serializeEffects(kitInventory.getEffects()));
        configFile.getConfiguration().set(path + ".game-rules.ranked", gameRules.isRanked());
        configFile.getConfiguration().set(path + ".game-rules.disablePartyFFA", gameRules.isDisablePartyFFA());
        configFile.getConfiguration().set(path + ".game-rules.disablePartySplit", gameRules.isDisablePartySplit());
        configFile.getConfiguration().set(path + ".game-rules.editable", gameRules.isEditable());
        configFile.getConfiguration().set(path + ".game-rules.antiFoodLoss", gameRules.isAntiFoodLoss());
        configFile.getConfiguration().set(path + ".game-rules.noItems", gameRules.isNoItems());
        configFile.getConfiguration().set(path + ".game-rules.build", gameRules.isBuild());
        configFile.getConfiguration().set(path + ".game-rules.bridge", gameRules.isBridge());
        configFile.getConfiguration().set(path + ".game-rules.spleef", gameRules.isSpleef());
        configFile.getConfiguration().set(path + ".game-rules.parkour", gameRules.isParkour());
        configFile.getConfiguration().set(path + ".game-rules.disable-fall-damage", gameRules.isDisableFallDamage());
        configFile.getConfiguration().set(path + ".game-rules.stickSpawn", gameRules.isStickSpawn());
        configFile.getConfiguration().set(path + ".game-rules.voidSpawn", gameRules.isVoidSpawn());
        configFile.getConfiguration().set(path + ".game-rules.bedwars", gameRules.isBedwars());
        configFile.getConfiguration().set(path + ".game-rules.mlgrush", gameRules.isMlgRush());
        configFile.getConfiguration().set(path + ".game-rules.combo", gameRules.isCombo());
        configFile.getConfiguration().set(path + ".game-rules.sumo", gameRules.isSumo());
        configFile.getConfiguration().set(path + ".game-rules.boxUHC", gameRules.isBoxUHC());
        configFile.getConfiguration().set(path + ".game-rules.timed", gameRules.isTimed());
        configFile.getConfiguration().set(path + ".game-rules.water-kill", gameRules.isWaterKill());
        configFile.getConfiguration().set(path + ".game-rules.lava-kill", gameRules.isLavaKill());
        configFile.getConfiguration().set(path + ".game-rules.health-regeneration", gameRules.isHealthRegeneration());
        configFile.getConfiguration().set(path + ".game-rules.infinite-speed", gameRules.isInfiniteSpeed());
        configFile.getConfiguration().set(path + ".game-rules.infinite-strength", gameRules.isInfiniteStrength());
        configFile.getConfiguration().set(path + ".game-rules.show-health", gameRules.isShowHealth());
        configFile.getConfiguration().set(path + ".game-rules.hit-delay", gameRules.getHitDelay());
        configFile.getConfiguration().set(path + ".game-rules.bow-hp", gameRules.isBowHP());
        configFile.getConfiguration().set(path + ".edit-rules.allow-potion-fill", editRules.isAllowPotionFill());

        try {
            configFile.getConfiguration().save(configFile.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateKitLeaderboards() {
        if (!this.getRankedEloLeaderboards().isEmpty()) this.getRankedEloLeaderboards().clear();
        Array.getInstance().getMongoThread().execute(() -> {
            for ( Document document : Profile.getCollection().find().sort(Sorts.descending("kitStatistics." + getName() + ".elo")).limit(10).into(new ArrayList<>()) ) {
                Document kitStatistics=(Document) document.get("kitStatistics");
                if (kitStatistics.containsKey(getName())) {
                    Document kitDocument=(Document) kitStatistics.get(getName());
                    LeaderboardsAdapter leaderboardsAdapter=new LeaderboardsAdapter();
                    leaderboardsAdapter.setName((String) document.get("name"));
                    leaderboardsAdapter.setElo((Integer) kitDocument.get("elo"));
                    this.getRankedEloLeaderboards().add(leaderboardsAdapter);
                }
            }
            if (!this.getWinLeaderboards().isEmpty()) this.getWinLeaderboards().clear();
            for ( Document document : Profile.getCollection().find().sort(Sorts.descending("kitStatistics." + getName() + ".unrankedWon")).limit(10).into(new ArrayList<>()) ) {
                Document kitStatistics=(Document) document.get("kitStatistics");
                if (kitStatistics.containsKey(getName())) {
                    Document kitDocument=(Document) kitStatistics.get(getName());
                    LeaderboardsAdapter leaderboardsAdapter=new LeaderboardsAdapter();
                    leaderboardsAdapter.setName((String) document.get("name"));
                    leaderboardsAdapter.setElo((Integer) kitDocument.get("won"));
                    this.getWinLeaderboards().add(leaderboardsAdapter);
                }
            }
        });
    }

}
