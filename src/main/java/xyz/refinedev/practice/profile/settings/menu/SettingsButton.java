package xyz.refinedev.practice.profile.settings.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.settings.SettingsType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/11/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SettingsButton extends Button {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("settings");

    private final SettingsType type;

    private String key = "BUTTONS.";

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        List<String> lines = new ArrayList<>();

        key += type.name() + ".";

        switch (type) {
            case TOGGLESCOREBOARD:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isScoreboardEnabled() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isScoreboardEnabled() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEDUELREQUESTS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isReceiveDuelRequests() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isReceiveDuelRequests() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEPINGFACTOR:
                if (player.hasPermission("array.profile.pingfactor")) {
                    for ( String text : config.getStringList(key + "LORE_PERMISSION" )) {
                        if (text.contains("<options>")) {
                            lines.add((profile.getSettings().isPingFactor() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isPingFactor() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                } else {
                    config.getStringList(key + "LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", plugin.getConfigHandler().getSTORE()))));
                }
                break;
            case TOGGLESPECTATORS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isAllowSpectators() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isAllowSpectators() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLELIGHTNING:
                if (player.hasPermission("array.profile.lightning")) {
                    for ( String text : config.getStringList(key + "LORE_PERMISSION" )) {
                        if (text.contains("<options>")) {
                            lines.add((profile.getSettings().isDeathLightning() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isDeathLightning() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                } else {
                    config.getStringList(key + "LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", plugin.getConfigHandler().getSTORE()))));
                }
                break;
            case TOGGLEPINGONSCOREBOARD:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isPingScoreboard() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isPingScoreboard() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLECPSONSCOREBOARD:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isCpsScoreboard() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isCpsScoreboard() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLETOURNAMENTMESSAGES:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isTmessagesEnabled() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isTmessagesEnabled() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLETABSTYLE:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isVanillaTab() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isVanillaTab() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLESHOWPLAYERS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isShowPlayers() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isShowPlayers() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEDROPPROTECT:
                if (player.hasPermission("array.profile.dropprotect")) {
                    for ( String text : config.getStringList(key + "LORE_PERMISSION" )) {
                        if (text.contains("<options>")) {
                            lines.add((profile.getSettings().isPreventSword() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isPreventSword() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                } else {
                    config.getStringList(key + "LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", plugin.getConfigHandler().getSTORE()))));
                }
                break;
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(config.getString(key + "MATERIAL")));
        itemBuilder.name(config.getString(key + "NAME"));
        itemBuilder.lore(lines);
        if (config.getInteger(key + "DATA") != 0) itemBuilder.durability(config.getInteger(key + "DATA"));

        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Player player, ClickType clickType) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        switch (type) {
            case TOGGLESCOREBOARD:
                Button.playSuccess(player);
                profile.getSettings().setScoreboardEnabled(!profile.getSettings().isScoreboardEnabled());
                break;
            case TOGGLEDUELREQUESTS:
                Button.playSuccess(player);
                profile.getSettings().setReceiveDuelRequests(!profile.getSettings().isReceiveDuelRequests());
                break;
            case TOGGLEPINGFACTOR:
                if (player.hasPermission("array.profile.pingfactor")) {
                    Button.playSuccess(player);
                    profile.getSettings().setPingFactor(!profile.getSettings().isPingFactor());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    Locale.ERROR_SETTING_NOPERM.toList().forEach(player::sendMessage);
                }
                break;
            case TOGGLESPECTATORS:
                Button.playSuccess(player);
                profile.getSettings().setAllowSpectators(!profile.getSettings().isAllowSpectators());
                break;
            case TOGGLECPSONSCOREBOARD:
                Button.playSuccess(player);
                profile.getSettings().setCpsScoreboard(!profile.getSettings().isCpsScoreboard());
                break;
            case TOGGLELIGHTNING:
                if (player.hasPermission("array.profile.lightning")) {
                    Button.playSuccess(player);
                    profile.getSettings().setDeathLightning(!profile.getSettings().isDeathLightning());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    Locale.ERROR_SETTING_NOPERM.toList().forEach(player::sendMessage);
                }
                break;
            case TOGGLEPINGONSCOREBOARD:
                Button.playSuccess(player);
                profile.getSettings().setPingScoreboard(!profile.getSettings().isPingScoreboard());
                break;
            case TOGGLETOURNAMENTMESSAGES:
                Button.playSuccess(player);
                profile.getSettings().setTmessagesEnabled(!profile.getSettings().isTmessagesEnabled());
                break;
            case TOGGLETABSTYLE:
                Button.playSuccess(player);
                profile.getSettings().setVanillaTab(!profile.getSettings().isVanillaTab());
                break;
            case TOGGLESHOWPLAYERS:
                Button.playSuccess(player);
                profile.getSettings().setShowPlayers(!profile.getSettings().isShowPlayers());
                profile.handleVisibility();
                break;
            case TOGGLEDROPPROTECT:
                if (player.hasPermission("array.profile.dropprotect")) {
                    Button.playSuccess(player);
                    profile.getSettings().setPreventSword(!profile.getSettings().isPreventSword());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    Locale.ERROR_SETTING_NOPERM.toList().forEach(player::sendMessage);
                }

        }
        TaskUtil.runAsync(profile::save);
    }

    /**
     * Should the click update the menu
     *
     * @param player The player clicking
     * @param clickType {@link ClickType}
     * @return {@link Boolean}
     */
    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}