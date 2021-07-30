package xyz.refinedev.practice.kit;

import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.leaderboards.KitLeaderboardsUpdateEvent;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/21/2021
 * Project: Array
 */


@Getter @Setter
public class Kit {
    
    @Getter private static final List<Kit> kits = new ArrayList<>();
    
    private static final Array plugin = Array.getInstance();
    @Getter @Setter private static Kit HCFTeamFight;

    private final List<LeaderboardsAdapter> rankedEloLeaderboards = new ArrayList<>();
    private final List<LeaderboardsAdapter> winLeaderboards = new ArrayList<>();
    private final List<LeaderboardsAdapter> killsLeaderboards = new ArrayList<>();
    private final List<ItemStack> editorItems = new ArrayList<>();

    private final KitInventory kitInventory = new KitInventory();
    private final KitGameRules gameRules = new KitGameRules();

    private final String name;
    private boolean enabled;
    private String knockbackProfile;
    private List<String> kitDescription;
    private ItemStack displayIcon;
    private String displayName;
    private Queue unrankedQueue;
    private Queue rankedQueue;
    private Queue clanQueue;

    public Kit(String name) {
        this.name = name;
        this.displayName = CC.RED + name;
        this.displayIcon = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.knockbackProfile = "default";
        this.kitDescription = new ArrayList<>();

        kits.add(this);
    }

    public void delete() {
        kits.remove(this);

        Queue.getQueues().remove(rankedQueue);
        if (rankedQueue != null )Queue.getQueues().remove(unrankedQueue);
        if (clanQueue != null) Queue.getQueues().remove(clanQueue);

        plugin.getKitsConfig().getConfiguration().set("kits." + getName(), null);
        plugin.getKitsConfig().save();
        
    }

    public static void preload() {
        FileConfiguration config = plugin.getKitsConfig().getConfiguration();

        try {
        if (plugin.getConfigHandler().isHCF_ENABLED()) {
            HCFTeamFight = new Kit("HCFTeamFight");
            HCFTeamFight.setDisplayIcon(new ItemBuilder(Material.BEACON).clearEnchantments().clearFlags().build());
            HCFTeamFight.save();
        }

        for (String key : config.getConfigurationSection("kits").getKeys(false)) {
            String path = "kits." + key;

            Kit kit = new Kit(key);

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
            kit.getGameRules().setClan(config.getBoolean(path + ".game-rules.clan"));
            kit.getGameRules().setDisablePartyFFA(!config.getBoolean(path + ".game-rules.party-ffa"));
            kit.getGameRules().setDisablePartySplit(!config.getBoolean(path + ".game-rules.party-split"));
            kit.getGameRules().setEditable(config.getBoolean(path + ".game-rules.editable"));
            kit.getGameRules().setAntiFoodLoss(!config.getBoolean(path + ".game-rules.hunger"));
            kit.getGameRules().setNoItems(config.getBoolean(path + ".game-rules.noItems"));
            kit.getGameRules().setBuild(config.getBoolean(path + ".game-rules.build"));
            kit.getGameRules().setBridge(config.getBoolean(path + ".game-rules.bridge"));
            kit.getGameRules().setSpleef(config.getBoolean(path + ".game-rules.spleef"));
            kit.getGameRules().setParkour(config.getBoolean(path + ".game-rules.parkour"));
            kit.getGameRules().setCombo(config.getBoolean(path + ".game-rules.combo"));
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
            kit.getGameRules().setHitDelay(config.getInt(path + ".game-rules.hit-delay"));

            if (config.getConfigurationSection(path + ".edit-rules.items") != null) {
                for (String itemKey : config.getConfigurationSection(path + ".edit-rules.items").getKeys(false)) {
                    kit.getEditorItems().add(
                            new ItemBuilder(Material.valueOf(config.getString(path + ".edit-rules.items." + itemKey + ".material")))
                                    .durability(config.getInt(path + ".edit-rules.items." + itemKey + ".durability"))
                                    .amount(config.getInt(path + ".edit-rules.items." + itemKey + ".amount"))
                                    .build());
                }
            }
        }

        kits.forEach(kit -> {
            if (kit.isEnabled()) {
                kit.setUnrankedQueue(new Queue(kit, QueueType.UNRANKED));

                if (kit.getGameRules().isRanked()) {
                    kit.setRankedQueue(new Queue(kit, QueueType.RANKED));
                }
                if (kit.getGameRules().isClan()) {
                    kit.setClanQueue(new Queue(kit, QueueType.CLAN));
                }
            }
        });

        try {
            Kit.getKits().forEach(Kit::updateKitLeaderboards);
        } catch (Exception e) {
            plugin.logger("&cThere was an error loading Leaderboards, Disabling Array!");
            Bukkit.shutdown();
        }

        } catch (Exception e) {
            plugin.logger("&cAn Error occured while loading Kits, please check kits.yml and try again.");
            Bukkit.shutdown();
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

        if (name.equals("HCFTeamFight")) return;

        BasicConfigurationFile configFile = plugin.getKitsConfig();
        configFile.getConfiguration().set(path + ".enabled", enabled);
        configFile.getConfiguration().set(path + ".display-name", displayName);
        configFile.getConfiguration().set(path + ".knockback-profile", knockbackProfile);
        configFile.getConfiguration().set(path + ".description", kitDescription);
        configFile.getConfiguration().set(path + ".icon.material", displayIcon.getType().name());
        configFile.getConfiguration().set(path + ".icon.durability", displayIcon.getDurability());
        configFile.getConfiguration().set(path + ".loadout.armor", InventoryUtil.serializeInventory(kitInventory.getArmor()));
        configFile.getConfiguration().set(path + ".loadout.contents", InventoryUtil.serializeInventory(kitInventory.getContents()));
        configFile.getConfiguration().set(path + ".loadout.effects", InventoryUtil.serializeEffects(kitInventory.getEffects()));
        configFile.getConfiguration().set(path + ".game-rules.ranked", gameRules.isRanked());
        configFile.getConfiguration().set(path + ".game-rules.clan", gameRules.isClan());
        configFile.getConfiguration().set(path + ".game-rules.party-ffa", !gameRules.isDisablePartyFFA());
        configFile.getConfiguration().set(path + ".game-rules.party-split", !gameRules.isDisablePartySplit());
        configFile.getConfiguration().set(path + ".game-rules.editable", gameRules.isEditable());
        configFile.getConfiguration().set(path + ".game-rules.hunger", !gameRules.isAntiFoodLoss());
        configFile.getConfiguration().set(path + ".game-rules.noitems", gameRules.isNoItems());
        configFile.getConfiguration().set(path + ".game-rules.build", gameRules.isBuild());
        configFile.getConfiguration().set(path + ".game-rules.bridge", gameRules.isBridge());
        configFile.getConfiguration().set(path + ".game-rules.spleef", gameRules.isSpleef());
        configFile.getConfiguration().set(path + ".game-rules.parkour", gameRules.isParkour());
        configFile.getConfiguration().set(path + ".game-rules.fall-damage", !gameRules.isDisableFallDamage());
        configFile.getConfiguration().set(path + ".game-rules.stickspawn", gameRules.isStickSpawn());
        configFile.getConfiguration().set(path + ".game-rules.voidspawn", gameRules.isVoidSpawn());
        configFile.getConfiguration().set(path + ".game-rules.mlgrush", gameRules.isMlgRush());
        configFile.getConfiguration().set(path + ".game-rules.combo", gameRules.isCombo());
        configFile.getConfiguration().set(path + ".game-rules.sumo", gameRules.isSumo());
        configFile.getConfiguration().set(path + ".game-rules.boxuhc", gameRules.isBoxuhc());
        configFile.getConfiguration().set(path + ".game-rules.timed", gameRules.isTimed());
        configFile.getConfiguration().set(path + ".game-rules.water-kill", gameRules.isWaterKill());
        configFile.getConfiguration().set(path + ".game-rules.lava-kill", gameRules.isLavaKill());
        configFile.getConfiguration().set(path + ".game-rules.health-regeneration", gameRules.isRegen());
        configFile.getConfiguration().set(path + ".game-rules.speed", gameRules.isSpeed());
        configFile.getConfiguration().set(path + ".game-rules.strength", gameRules.isStrength());
        configFile.getConfiguration().set(path + ".game-rules.show-health", gameRules.isShowHealth());
        configFile.getConfiguration().set(path + ".game-rules.hit-delay", gameRules.getHitDelay());
        configFile.getConfiguration().set(path + ".game-rules.bow-hp", gameRules.isBowHP());

        try {
            configFile.getConfiguration().save(configFile.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateKitLeaderboards() {
        if (!this.getRankedEloLeaderboards().isEmpty()) this.getRankedEloLeaderboards().clear();
        if (!this.getWinLeaderboards().isEmpty()) this.getWinLeaderboards().clear();
        plugin.getMongoThread().execute(() -> {
            for (Document document : Profile.getCollection().find().sort(Sorts.descending("kitStatistics." + getName() + ".elo")).limit(10).into(new ArrayList<>())) {
                Document kitStatistics = (Document) document.get("kitStatistics");
                if (kitStatistics.containsKey(getName())) {
                    Document kitDocument = (Document) kitStatistics.get(getName());
                    LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                    leaderboardsAdapter.setName((String) document.get("name"));
                    leaderboardsAdapter.setElo((Integer) kitDocument.get("elo"));
                    if (!getRankedEloLeaderboards().isEmpty()) {
                        getRankedEloLeaderboards().removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
                    }
                    this.getRankedEloLeaderboards().add(leaderboardsAdapter);
                }
            }
            for (Document document : Profile.getCollection().find().sort(Sorts.descending("kitStatistics." + getName() + ".won")).limit(10).into(new ArrayList<>())) {
                Document kitStatistics = (Document) document.get("kitStatistics");
                if (kitStatistics.containsKey(getName())) {
                    Document kitDocument = (Document) kitStatistics.get(getName());
                    LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                    leaderboardsAdapter.setName((String) document.get("name"));
                    leaderboardsAdapter.setElo((Integer) kitDocument.get("won"));
                    if (!getWinLeaderboards().isEmpty()) {
                        getWinLeaderboards().removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
                    }
                    this.getWinLeaderboards().add(leaderboardsAdapter);
                }
            }
            });
        new KitLeaderboardsUpdateEvent().call();
    }

    public boolean isParty() {
        return (!gameRules.isDisablePartyFFA() && !gameRules.isParkour() && !gameRules.isBridge() && !gameRules.isDisablePartySplit() && isEnabled());
    }

    public void applyToPlayer(Player player) {
        player.getInventory().setArmorContents(getKitInventory().getArmor());
        player.getInventory().setContents(getKitInventory().getContents());
        player.updateInventory();
    }
}
