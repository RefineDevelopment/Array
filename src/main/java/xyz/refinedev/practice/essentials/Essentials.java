package xyz.refinedev.practice.essentials;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.essentials.meta.*;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.menu.MenuUpdateTask;
import xyz.refinedev.practice.util.other.Description;
import xyz.refinedev.practice.util.other.PiracyTask;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/16/2021
 * Project: Array
 */

public class Essentials {

    public static BasicConfigurationFile config = Array.getInstance().getMainConfig();

    @Getter @Setter private static Location spawn;

    @Getter private static final List<String> motd = new ArrayList<>();
    @Getter private static final List<String> queueLore = new ArrayList<>();
    @Getter private static final EssentialsMeta meta = new EssentialsMeta();
    @Getter private static final DefaultMeta defaultMeta = new DefaultMeta();
    @Getter private static final SocialMeta socialMeta = new SocialMeta();
    @Getter private static final NametagMeta nametagMeta = new NametagMeta();

    @Getter @Setter private static boolean isOutdated = false;
    @Getter @Setter private static String newVersion = Description.getVersion();

    @Getter public static String license = config.getStringOrDefault("LICENSE", "XXXX-XXXX-XXXX");

    public void init() {
        PiracyMeta piracyMeta = new PiracyMeta(Array.getInstance(), license);
        piracyMeta.verify();
    }

    public static void setupEssentials() {
        Arrays.asList(
                Material.WORKBENCH,
                Material.STICK,
                Material.WOOD_PLATE,
                Material.WOOD_BUTTON,
                Material.SNOW_BLOCK
        ).forEach(InventoryUtil::removeCrafting);

        for ( World world : Array.getInstance().getServer().getWorlds() ) {
            world.setDifficulty(Difficulty.HARD);
        }
        
        TaskUtil.runTimerAsync(new MenuUpdateTask(), 20L, 50L);

        Essentials.load();
        Essentials.save();
    }

    public static void load() {
        String key = "ESSENTIAL.";

        if (!motd.isEmpty()) motd.clear();
        if (!queueLore.isEmpty()) queueLore.clear();

        spawn = LocationUtil.deserialize(config.getStringOrDefault(key + "SPAWN", "world:0:60:0:-89.59775:0.17956273"));
        meta.setHCFEnabled(config.getBoolean(key + "HCF_ENABLED"));
        meta.setCoreHookEnabled(config.getBoolean(key + "CORE_HOOK"));
        meta.setTabEnabled(config.getBoolean(key + "TAB_ENABLED"));
        meta.setRankedEnabled(config.getBoolean(key + "ARENA_RATING"));
        meta.setVoidSpawnLevel(config.getInteger(key + "VOIDSPAWN_YLEVEL"));
        meta.setFfaSpawnRadius(config.getInteger(key + "FFA_CIRCLE_RADIUS"));
        meta.setTeleportDelay(config.getInteger(key + "TELEPORT_DELAY"));
        meta.setMatchSpawnLevel(config.getInteger(key + "MATCH_YLEVEL_SPAWN_ADD"));
        meta.setBridgeClearBlocks(config.getBoolean(key + "BRIDGE_CLEARBLOCKS"));
        meta.setDisclaimerEnabled(config.getBoolean(key + "DISCLAIMER_MESSAGE_ENABLED"));
        meta.setRemoveBottles(config.getBoolean(key + "REMOVE_BOTTLES"));

        meta.setUpdateNotification(config.getBoolean("UPDATE_NOTIFICATION"));

        meta.setMotdEnabled(config.getBoolean(key + "JOIN_MESSAGE_ENABLED"));
        motd.addAll(config.getStringList(key + "JOIN_MESSAGE"));

        queueLore.addAll(config.getStringList(key + "QUEUE_LORE"));

        meta.setRankedEnabled(config.getBoolean("RANKED.ENABLED"));
        meta.setRequireKills(config.getBoolean("RANKED.REQUIREKILLS"));
        meta.setRequiredKills(config.getInteger("RANKED.REQUIREDKILLS"));
        meta.setLimitPing(config.getBoolean("RANKED.LIMIT_PING"));
        meta.setPingLimit(config.getInteger("RANKED.PING-LIMIT"));

        meta.setEnderpearlCooldown(config.getInteger("COOLDOWNS.ENDER_PEARL"));
        meta.setBowCooldown(config.getInteger("COOLDOWNS.BOW"));

        //Socials
        socialMeta.setDiscord(config.getString("SOCIAL.DISCORD"));
        socialMeta.setWebiste(config.getString("SOCIAL.WEBSITE"));
        socialMeta.setStore(config.getString("SOCIAL.STORE"));
        socialMeta.setTwitter(config.getString("SOCIAL.TWITTER"));

        //NameTags
        nametagMeta.setEnabled(config.getBoolean("NAMETAGS.ENABLED"));

        Essentials.loadNametags();
        Essentials.loadDefaultOptions();
    }


    public static void save() {
        String key = "ESSENTIAL.";

        //Essentials
        config.set(key + "SPAWN", spawn == null ? "world:0:60:0:-89.59775:0.17956273" : LocationUtil.serialize(spawn));
        config.set(key + "HCF_ENABLED", meta.isHCFEnabled());
        config.set(key + "CORE_HOOK", meta.isCoreHookEnabled());
        config.set(key + "TAB_ENABLED", meta.isTabEnabled());
        config.set(key + "ARENA_RATING", meta.isRatingEnabled());
        config.set(key + "VOIDSPAWN_YLEVEL", meta.getVoidSpawnLevel());
        config.set(key + "MATCH_YLEVEL_SPAWN_ADD", meta.getMatchSpawnLevel());
        config.set(key + "FFA_CIRCLE_RADIUS", meta.getFfaSpawnRadius());
        config.set(key + "TELEPORT_DELAY", meta.getTeleportDelay());
        config.set(key + "BRIDGE_CLEARBLOCKS", meta.isBridgeClearBlocks());
        config.set(key + "REMOVE_BOTTLES", meta.isRemoveBottles());
        config.set(key + "DISCLAIMER_MESSAGE_ENABLED", meta.isDisclaimerEnabled());
        config.set(key + "JOIN_MESSAGE_ENABLED", meta.isMotdEnabled());
        config.set(key + "JOIN_MESSAGE", motd);
        config.set(key + "QUEUE_LORE", queueLore);

        //Ranked
        config.set("RANKED.ENABLED", meta.isRankedEnabled());
        config.set("RANKED.REQUIREKILLS", meta.isRequireKills());
        config.set("RANKED.REQUIREDKILLS", meta.getRequiredKills());
        config.set("RANKED.LIMIT_PING", meta.isLimitPing());
        config.set("RANKED.PING-LIMIT", meta.getPingLimit());

        config.set("COOLDOWNS.ENDER_PEARL", meta.getEnderpearlCooldown());
        config.set("COOLDOWNS.BOW", meta.getBowCooldown());

        //Socials
        config.set("SOCIAL.DISCORD", socialMeta.getDiscord());
        config.set("SOCIAL.WEBSITE", socialMeta.getWebiste());
        config.set("SOCIAL.STORE", socialMeta.getStore());
        config.set("SOCIAL.TWITTER", socialMeta.getTwitter());
        config.save();
    }


    private static void loadNametags() {
        nametagMeta.setDefaultColor(config.getStringOrDefault("NAMETAGS.DEFAULT", "<rank_color>"));
        if (!nametagMeta.getDefaultColor().equalsIgnoreCase("<rank_color>")) {
            try {
                ChatColor.valueOf(nametagMeta.getDefaultColor());
            } catch (Exception e) {
                nametagMeta.setDefaultColor("<rank_color>");
                Array.logger("&cInvalid Color setup for Default NameTags, retreating to default color (Rank Color)");
            }
        }
        try {
            nametagMeta.setEventColor(ChatColor.valueOf(config.getString("NAMETAGS.EVENT")));
        } catch (Exception e) {
            nametagMeta.setEventColor(ChatColor.AQUA);
            Array.logger("&cInvalid Color setup for Event NameTags, retreating to default event color (AQUA)");
        }
        try {
            nametagMeta.setPartyColor(ChatColor.valueOf(config.getString("NAMETAGS.PARTY")));
        } catch (Exception e) {
            nametagMeta.setEventColor(ChatColor.BLUE);
            Array.logger("&cInvalid Color setup for Party NameTags, retreating to default party color (Blue)");
        }
    }

    public static void loadDefaultOptions() {
        String key = "DEFAULT_SETTINGS.";

        defaultMeta.setScoreboardEnabled(config.getBoolean(key + "SCOREBOARD_ENABLED"));
        defaultMeta.setAllowSpectators(config.getBoolean(key + "ALLOW_SPECTATORS"));
        defaultMeta.setReceiveDuelRequests(config.getBoolean(key + "DUEL_REQUESTS"));
        defaultMeta.setCpsScoreboard(config.getBoolean(key + "SCOREBOARD_CPS"));
        defaultMeta.setPingScoreboard(config.getBoolean(key + "SCOREBOARD_PING"));
        defaultMeta.setDurationScoreboard(config.getBoolean(key + "SCOREBOARD_DURATION"));
        defaultMeta.setPingFactor(config.getBoolean(key + "PING_FACTOR"));
        defaultMeta.setPreventSword(config.getBoolean(key + "DROP_PROTECT"));
        defaultMeta.setShowPlayers(config.getBoolean(key + "PLAYER_VISIBILITY"));
        defaultMeta.setVanillaTab(config.getBoolean(key + "VANILLA_TAB"));
        defaultMeta.setTmessagesEnabled(config.getBoolean(key + "TOURNEY_MESSAGES"));
    }
}
