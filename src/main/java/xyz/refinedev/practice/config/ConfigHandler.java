package xyz.refinedev.practice.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.other.PiracyMeta;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.menu.MenuUpdateTask;
import xyz.refinedev.practice.util.other.Description;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/27/2021
 * Project: Array
 */

@Getter @Setter
public class ConfigHandler {
    
    private final Array plugin;
    private final BasicConfigurationFile config;

    private String DISCORD, WEBSITE, TWITTER, STORE;
    private String NEW_VERSION;
    private String LICENSE;

    private List<String> QUEUE_LORE = new ArrayList<>();
    private List<String> JOIN_MESSAGE = new ArrayList<>();

    private Location spawn;

    public String defaultColor = "<rank_color>";
    public ChatColor partyColor = ChatColor.BLUE;
    public ChatColor eventColor = ChatColor.RED;

    private boolean HCF_ENABLED, TAB_ENABLED, CORE_HOOK_ENABLED, JOIN_MESSAGE_ENABLED, DISCLAIMER_ENABLED, REMOVED_BOTTLES, NAMETAGS_ENABLED,
                    RANKED_ENABLED, RATINGS_ENABLED, REQUIRE_KILLS, UPDATE_NOTIFICATION, LIMIT_PING, BRIDGE_CLEAR_BLOCKS, OUTDATED = false;

    private int PING_LIMIT, REQUIRED_KILLS, FFA_SPAWN_RADIUS, VOID_SPAWN_YLEVEL, MATCH_COUNT_DOWN, ENDERPEARL_COOLDOWN,
                BOW_COOLDOWN, TELEPORT_DELAY, MATCH_SPAWN_YLEVEL;

    public ConfigHandler(Array plugin) {
        this.plugin = plugin;
        this.config = plugin.getMainConfig();
    }

    public void init() {
        this.NEW_VERSION = Description.getVersion();
        this.LICENSE = config.getStringOrDefault("LICENSE", "XXXX-XXXX-XXXX-XXXX");

        PiracyMeta piracyMeta = new PiracyMeta(this.plugin, this.getLICENSE());
        piracyMeta.verify();

        final String key = "SETTINGS.";

        if (!JOIN_MESSAGE.isEmpty()) JOIN_MESSAGE.clear();
        if (!QUEUE_LORE.isEmpty()) QUEUE_LORE.clear();

        spawn = LocationUtil.deserialize(config.getStringOrDefault(key + "SPAWN", "world:0:60:0:-89.59775:0.17956273"));

        HCF_ENABLED = config.getBoolean(key + "HCF_ENABLED");
        CORE_HOOK_ENABLED = config.getBoolean(key + "CORE_HOOK");
        TAB_ENABLED = config.getBoolean(key + "TAB_ENABLED");
        RANKED_ENABLED = config.getBoolean(key + "ARENA_RATING");
        VOID_SPAWN_YLEVEL = config.getInteger(key + "VOIDSPAWN_YLEVEL");
        FFA_SPAWN_RADIUS = config.getInteger(key + "FFA_CIRCLE_RADIUS");
        MATCH_COUNT_DOWN = config.getInteger(key + "MATCH_COUNTDOWN");
        TELEPORT_DELAY = config.getInteger(key + "TELEPORT_DELAY");
        MATCH_SPAWN_YLEVEL = config.getInteger(key + "MATCH_SPAWN_YLEVEL_ADD");
        BRIDGE_CLEAR_BLOCKS = config.getBoolean(key + "BRIDGE_CLEARBLOCKS");
        DISCLAIMER_ENABLED = config.getBoolean(key + "DISCLAIMER_MESSAGE_ENABLED");
        REMOVED_BOTTLES = config.getBoolean(key + "REMOVE_BOTTLES");

        JOIN_MESSAGE_ENABLED = config.getBoolean("JOIN_MESSAGE.ENABLED");
        JOIN_MESSAGE.addAll(config.getStringList("JOIN_MESSAGE.MESSAGE"));

        QUEUE_LORE.addAll(config.getStringList("QUEUE_LORE"));

        RANKED_ENABLED = config.getBoolean("RANKED.ENABLED");
        REQUIRE_KILLS = config.getBoolean("RANKED.REQUIREKILLS");
        REQUIRED_KILLS = config.getInteger("RANKED.REQUIREDKILLS");
        LIMIT_PING = config.getBoolean("RANKED.LIMIT_PING");
        PING_LIMIT = config.getInteger("RANKED.PING-LIMIT");

        ENDERPEARL_COOLDOWN = config.getInteger("COOLDOWNS.ENDER_PEARL");
        BOW_COOLDOWN = config.getInteger("COOLDOWNS.BOW");

        DISCORD = config.getString("SOCIAL.DISCORD");
        WEBSITE = config.getString("SOCIAL.WEBSITE");
        STORE = config.getString("SOCIAL.STORE");
        TWITTER = config.getString("SOCIAL.TWITTER");

        NAMETAGS_ENABLED = config.getBoolean("NAMETAGS.ENABLED");
        if (NAMETAGS_ENABLED) this.loadNametags();

        UPDATE_NOTIFICATION = config.getBoolean("UPDATE_NOTIFICATION");
    }

    public void setupEssentials() {
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
    }

    private void loadNametags() {
        defaultColor = config.getStringOrDefault("NAMETAGS.DEFAULT", "<rank_color>");
        if (!getDefaultColor().equalsIgnoreCase("<rank_color>")) {
            try {
                ChatColor.valueOf(getDefaultColor());
            } catch (Exception e) {
                defaultColor = "<rank_color>";
                plugin.logger("&cInvalid Color setup for Default NameTags, retreating to default color (Rank Color)");
            }
        }
        try {
            eventColor = ChatColor.valueOf(config.getString("NAMETAGS.EVENT"));
        } catch (Exception e) {
            eventColor = ChatColor.AQUA;
            plugin.logger("&cInvalid Color setup for Event NameTags, retreating to default event color (AQUA)");
        }
        try {
            partyColor = ChatColor.valueOf(config.getString("NAMETAGS.PARTY"));
        } catch (Exception e) {
            eventColor = ChatColor.BLUE;
            plugin.logger("&cInvalid Color setup for Party NameTags, retreating to default party color (Blue)");
        }
    }

    public void save() {
        String key = "SETTINGS.";

        //Essentials
        config.set(key + "SPAWN", spawn == null ? "world:0:60:0:-89.59775:0.17956273" : LocationUtil.serialize(spawn));

        config.set(key + "HCF_ENABLED", HCF_ENABLED);
        config.set(key + "CORE_HOOK", CORE_HOOK_ENABLED);
        config.set(key + "TAB_ENABLED", TAB_ENABLED);
        config.set(key + "ARENA_RATING", RATINGS_ENABLED);
        config.set(key + "VOIDSPAWN_YLEVEL", VOID_SPAWN_YLEVEL);
        config.set(key + "MATCH_SPAWN_YLEVEL_ADD", MATCH_SPAWN_YLEVEL);
        config.set(key + "FFA_CIRCLE_RADIUS", FFA_SPAWN_RADIUS);
        config.set(key + "TELEPORT_DELAY", TELEPORT_DELAY);
        config.set(key + "BRIDGE_CLEARBLOCKS", BRIDGE_CLEAR_BLOCKS);
        config.set(key + "REMOVE_BOTTLES", REMOVED_BOTTLES);
        config.set(key + "DISCLAIMER_MESSAGE_ENABLED", DISCLAIMER_ENABLED);

        config.set("QUEUE_LORE", QUEUE_LORE);

        config.set("JOIN_MESSAGE.ENABLED", JOIN_MESSAGE_ENABLED);
        config.set("JOIN_MESSAGE.MESSAGE", JOIN_MESSAGE);

        //Ranked
        config.set("RANKED.ENABLED", RANKED_ENABLED);
        config.set("RANKED.REQUIREKILLS", REQUIRE_KILLS);
        config.set("RANKED.REQUIREDKILLS", REQUIRED_KILLS);
        config.set("RANKED.LIMIT_PING", LIMIT_PING);
        config.set("RANKED.PING-LIMIT", PING_LIMIT);

        config.set("COOLDOWNS.ENDER_PEARL", ENDERPEARL_COOLDOWN);
        config.set("COOLDOWNS.BOW", BOW_COOLDOWN);

        //Socials
        config.set("SOCIAL.DISCORD", DISCORD);
        config.set("SOCIAL.WEBSITE", WEBSITE);
        config.set("SOCIAL.STORE", STORE);
        config.set("SOCIAL.TWITTER", TWITTER);

        config.set("UPDATE_NOTIFICATION", UPDATE_NOTIFICATION);

        config.save();
    }
}
