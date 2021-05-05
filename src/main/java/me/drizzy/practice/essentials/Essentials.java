package me.drizzy.practice.essentials;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.essentials.event.SpawnTeleportEvent;
import me.drizzy.practice.essentials.meta.EssentialsMeta;
import me.drizzy.practice.essentials.meta.NametagMeta;
import me.drizzy.practice.essentials.meta.SocialMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.config.Lang;
import me.drizzy.practice.util.location.LocationUtil;
import me.drizzy.practice.util.menu.MenuUpdateTask;
import me.drizzy.practice.util.other.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Drizzy
 * Created at 4/16/2021
 */
@Getter
@Setter
public class Essentials {

    public static BasicConfigurationFile config = Array.getInstance().getMainConfig();

    public Location spawn;
    public final List<String> motd = new ArrayList<>();
    public final List<String> queueLore = new ArrayList<>();
    public final EssentialsMeta meta = new EssentialsMeta();
    public final SocialMeta socialMeta = new SocialMeta();
    public final NametagMeta nametagMeta = new NametagMeta();

    public void setupEssentials() {
        TaskUtil.runTimerAsync(new MenuUpdateTask(), 20L, 20L);
        this.load();
        this.save();
    }

    public void load() {
        String key = "ESSENTIAL.";

        spawn=LocationUtil.deserialize(config.getStringOrDefault(key + "SPAWN", "world:0:60:0:-89.59775:0.17956273"));
        meta.setHCFEnabled(config.getBoolean(key + "HCF_ENABLED"));
        meta.setCoreHookEnabled(config.getBoolean(key + "CORE_HOOK"));
        meta.setTabEnabled(config.getBoolean(key + "TAB_ENABLED"));
        meta.setVoidSpawnLevel(config.getInteger(key + "VOIDSPAWN_YLEVEL"));
        meta.setFfaSpawnRadius(config.getInteger(key + "FFA_CIRCLE_RADIUS"));
        meta.setBridgeClearBlocks(config.getBoolean(key + "BRIDGE_CLEARBLOCKS"));
        meta.setDisclaimerEnabled(config.getBoolean(key + "DISCLAIMER_MESSAGE_ENABLED"));

        meta.setMotdEnabled(config.getBoolean(key + "JOIN_MESSAGE_ENABLED"));
        motd.addAll(config.getStringList(key + "JOIN_MESSAGE"));

        meta.setRankedEnabled(config.getBoolean("RANKED.ENABLED"));
        meta.setRequireKills(config.getBoolean("RANKED.REQUIREKILLS"));
        meta.setRequiredKills(config.getInteger("RANKED.REQUIREDKILLS"));
        meta.setLimitPing(config.getBoolean("RANKED.LIMIT_PING"));
        meta.setPingLimit(config.getInteger("RANKED.PING-LIMIT"));
        queueLore.addAll(config.getStringList("QUEUE_LORE"));

        //Socials
        socialMeta.setDiscord(config.getStringOrDefault("SCOCIAL.DISCORD", "discord.link/purge"));
        socialMeta.setWebiste(config.getStringOrDefault("SCOCIAL.WEBSITE", "www.purgecommunity.com"));
        socialMeta.setStore(config.getStringOrDefault("SOCIAL.STORE", "store.purgecommunity.com"));
        socialMeta.setTwitter(config.getStringOrDefault("SOCIAL.TWITTER", "@PurgeCommunity"));

        //Nametags
        nametagMeta.setEnabled(config.getBoolean("NAMETAGS.ENABLED"));
        this.loadNametags();
    }


    public void save() {
        YamlConfiguration configuration = config.getConfiguration();
        String key = "ESSENTIAL.";

        //Essentials
        configuration.set(key + "SPAWN", LocationUtil.serialize(spawn));
        configuration.set(key + "HCF_ENABLED", meta.isHCFEnabled());
        configuration.set(key + "CORE_HOOK", meta.isCoreHookEnabled());
        configuration.set(key + "TAB_ENABLED", meta.isTabEnabled());
        configuration.set(key + "VOIDSPAWN_YLEVEL", meta.getVoidSpawnLevel());
        configuration.set(key + "FFA_CIRCLE_RADIUS", meta.getFfaSpawnRadius());
        configuration.set(key + "BRIDGE_CLEARBLOCKS", meta.isBridgeClearBlocks());
        configuration.set(key + "DISCLAIMER_MESSAGE_ENABLED", meta.isDisclaimerEnabled());
        configuration.set(key + "JOIN_MESSAGE_ENABLED", meta.isMotdEnabled());
        configuration.set(key + "JOIN_MESSAGE", motd);
        configuration.set("QUEUE_LORE", queueLore);

        //Ranked
        configuration.set("RANKED.ENABLED", meta.isRankedEnabled());
        configuration.set("RANKED.REQUIREKILLS", meta.isRequireKills());
        configuration.set("RANKED.REQUIREDKILLS", meta.getRequiredKills());
        configuration.set("RANKED.LIMIT_PING", meta.isLimitPing());
        configuration.set("RANKED.PING-LIMIT", meta.getPingLimit());

        //Socials
        configuration.set("SOCIAL.DISCORD", socialMeta.getDiscord());
        configuration.set("SOCIAL.WEBSITE", socialMeta.getWebiste());
        configuration.set("SOCIAL.STORE", socialMeta.getStore());
        configuration.set("SOCIAL.TWITTER", socialMeta.getTwitter());
        config.save();
    }


    private void loadNametags() {
        nametagMeta.setDefaultColor(config.getStringOrDefault("NAMETAGS.DEFAULT", "<rank_color>"));
        if (!nametagMeta.getDefaultColor().equalsIgnoreCase("<rank_color>")) {
            try {
                ChatColor.valueOf(nametagMeta.getDefaultColor());
            } catch (Exception e) {
                nametagMeta.setDefaultColor("<rank_color>");
                Array.logger("&cInvalid Color setup for Default Nametags, retreating to default color (Rank Color)");
            }
        }
        try {
            nametagMeta.setEventColor(ChatColor.valueOf(config.getString("NAMETAGS.EVENT")));
        } catch (Exception e) {
            nametagMeta.setEventColor(ChatColor.AQUA);
            Array.logger("&cInvalid Color setup for Event Nametags, retreating to default event color (AQUA)");
        }
        try {
            nametagMeta.setPartyColor(ChatColor.valueOf(config.getString("NAMETAGS.PARTY")));
        } catch (Exception e) {
            nametagMeta.setEventColor(ChatColor.BLUE);
            Array.logger("&cInvalid Color setup for Party Nametags, retreating to default party color (Blue)");
        }
    }
}
