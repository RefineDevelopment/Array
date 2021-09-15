package xyz.refinedev.practice.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.match.types.kit.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.TeamBridgeMatch;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;

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
    @Getter @Setter private static Kit HCFTeamFight;

    private static final Array plugin = Array.getInstance();

    private final List<ItemStack> editorItems = new ArrayList<>();
    private List<LeaderboardsAdapter> eloLeaderboards = new ArrayList<>();
    private List<LeaderboardsAdapter> winLeaderboards = new ArrayList<>();

    private final KitGameRules gameRules = new KitGameRules();
    private KitInventory kitInventory = new KitInventory();

    private final String name;
    private boolean enabled;
    private String knockbackProfile;
    private List<String> kitDescription;
    private ItemStack displayIcon;
    private String displayName;
    private Queue unrankedQueue, rankedQueue, clanQueue;

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

        plugin.getKitsConfig().set("kits." + getName(), null);
        plugin.getKitsConfig().save();
    }

    public static void preload() {
        BasicConfigurationFile config = plugin.getKitsConfig();

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
            kit.getGameRules().setHitDelay(config.getInteger(path + ".game-rules.hit-delay"));

            if (config.getConfigurationSection(path + ".edit-rules.items") != null) {
                for (String itemKey : config.getConfigurationSection(path + ".edit-rules.items").getKeys(false)) {
                    String pathKey = path + ".edit-rules.items." + itemKey;
                    kit.getEditorItems().add(new ItemBuilder(Material.valueOf(config.getString(pathKey + ".material")))
                                    .durability(config.getInteger(pathKey + ".durability"))
                                    .amount(config.getInteger(pathKey + ".amount"))
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
        configFile.set(path + ".enabled", enabled);
        configFile.set(path + ".display-name", displayName);
        configFile.set(path + ".knockback-profile", knockbackProfile);
        configFile.set(path + ".description", kitDescription);

        configFile.set(path + ".icon.material", displayIcon.getType().name());
        configFile.set(path + ".icon.durability", displayIcon.getDurability());

        configFile.set(path + ".loadout.armor", InventoryUtil.serializeInventory(kitInventory.getArmor()));
        configFile.set(path + ".loadout.contents", InventoryUtil.serializeInventory(kitInventory.getContents()));
        configFile.set(path + ".loadout.effects", InventoryUtil.serializeEffects(kitInventory.getEffects()));

        configFile.set(path + ".game-rules.ranked", gameRules.isRanked());
        configFile.set(path + ".game-rules.clan", gameRules.isClan());
        configFile.set(path + ".game-rules.party-ffa", !gameRules.isDisablePartyFFA());
        configFile.set(path + ".game-rules.party-split", !gameRules.isDisablePartySplit());
        configFile.set(path + ".game-rules.editable", gameRules.isEditable());
        configFile.set(path + ".game-rules.hunger", !gameRules.isAntiFoodLoss());
        configFile.set(path + ".game-rules.noitems", gameRules.isNoItems());
        configFile.set(path + ".game-rules.build", gameRules.isBuild());
        configFile.set(path + ".game-rules.bridge", gameRules.isBridge());
        configFile.set(path + ".game-rules.spleef", gameRules.isSpleef());
        configFile.set(path + ".game-rules.parkour", gameRules.isParkour());
        configFile.set(path + ".game-rules.fall-damage", !gameRules.isDisableFallDamage());
        configFile.set(path + ".game-rules.stickspawn", gameRules.isStickSpawn());
        configFile.set(path + ".game-rules.voidspawn", gameRules.isVoidSpawn());
        configFile.set(path + ".game-rules.mlgrush", gameRules.isMlgRush());
        configFile.set(path + ".game-rules.combo", gameRules.isCombo());
        configFile.set(path + ".game-rules.sumo", gameRules.isSumo());
        configFile.set(path + ".game-rules.boxuhc", gameRules.isBoxuhc());
        configFile.set(path + ".game-rules.timed", gameRules.isTimed());
        configFile.set(path + ".game-rules.water-kill", gameRules.isWaterKill());
        configFile.set(path + ".game-rules.lava-kill", gameRules.isLavaKill());
        configFile.set(path + ".game-rules.health-regeneration", gameRules.isRegen());
        configFile.set(path + ".game-rules.speed", gameRules.isSpeed());
        configFile.set(path + ".game-rules.strength", gameRules.isStrength());
        configFile.set(path + ".game-rules.show-health", gameRules.isShowHealth());
        configFile.set(path + ".game-rules.hit-delay", gameRules.getHitDelay());
        configFile.set(path + ".game-rules.bow-hp", gameRules.isBowHP());

        configFile.save();
    }

    public boolean isParty() {
        return (!gameRules.isDisablePartyFFA() && !gameRules.isParkour() && !gameRules.isBridge() && !gameRules.isDisablePartySplit() && isEnabled());
    }

    public void applyToPlayer(Player player) {
        player.getInventory().setArmorContents(getKitInventory().getArmor());
        player.getInventory().setContents(getKitInventory().getContents());
        player.updateInventory();
    }

    public Match createSoloKitMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        if (gameRules.isBridge()) {
            return new SoloBridgeMatch(queue, playerA, playerB, kit, arena, queueType);
        } else if (gameRules.isBedwars()) {
            //
        } else if (gameRules.isMlgRush()) {
            //
        } else if (gameRules.isBoxing()) {
            //
        }
        return new SoloMatch(queue, playerA, playerB, kit, arena, queueType);
    }

    public Match createTeamKitMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
        if (gameRules.isBridge()) {
            return new TeamBridgeMatch(teamA, teamB, kit, arena);
        } else if (gameRules.isBedwars()) {
            //
        }
        return new TeamMatch(teamA, teamB, kit, arena);
    }
}
