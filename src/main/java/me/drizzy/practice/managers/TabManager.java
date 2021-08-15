package me.drizzy.practice.managers;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.other.TaskUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Drizzy
 * Created at 4/16/2021
 */
@Getter
@Setter
public class TabManager {

    private final BasicConfigurationFile config = Array.getInstance().getTablistConfig();
    private final String key = "TAB.";

    public String mainColor;
    public String secondaryColor;
    public String header;
    public ChatColor dotColor;
    public String legacyHeader;
    public String footer;

    /**
     * Load our strings from the Config
     */
    public void load() {
        setMainColor(config.getStringOrDefault(key + "MAIN_COLOR", "&c").replace("%splitter%", "┃"));
        setSecondaryColor(config.getStringOrDefault(key + "SECONDARY_COLOR", "&f").replace("%splitter%", "┃"));
        setHeader(config.getStringOrDefault(key + "HEADER", "\n&c&lArray Practice\n&7&opurgecommunity.com\n").replace("%splitter%", "┃"));
        setFooter(config.getStringOrDefault(key + "FOOTER", "\n&c&oYou can buy ranks and perks at\n&7<store>\n").replace("<store>", Array.getInstance().getEssentials().getSocialMeta().getStore()).replace("%splitter%", "┃"));
        setLegacyHeader(config.getStringOrDefault(key + "1DOT7_HEADER", mainColor + "&lPractice &7| " + secondaryColor + "&lEU").replace("|", "┃").replace("%splitter%", "┃"));
        try {
            setDotColor(ChatColor.valueOf(config.getStringOrDefault(key + "DOT_COLOR", "RED")));
        } catch (Exception e) {
            setDotColor(ChatColor.RED);
            Array.logger("&cInvalid Dot Color setup for Tablist, falling back to default &7(RED)");
        }
    }

    /**
     * Save our values to the Config
     */
    public void save() {
        YamlConfiguration configuration = config.getConfiguration();

        configuration.set(key + "MAIN_COLOR", mainColor);
        configuration.set(key + "SECONDARY_COLOR", secondaryColor);
        configuration.set(key + "HEADER", header);
        configuration.set(key + "FOOTER", footer);
        configuration.set(key + "1DOT7_HEADER", legacyHeader);
        configuration.set(key + "DOT_COLOR", dotColor.name());
    }



}
