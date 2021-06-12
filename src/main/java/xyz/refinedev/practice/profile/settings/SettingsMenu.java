package xyz.refinedev.practice.profile.settings;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.other.TaskUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 3/12/2021
 * Project: Array
 */

public class SettingsMenu extends Menu {

    private final static BasicConfigurationFile config = Array.getInstance().getMenuConfig();

    @Override
    public String getTitle(Player player) {
        return config.getString("MENUS.SETTINGS.TITLE");
    }

    @Override
    public int getSize() {
        return config.getInteger("MENUS.SETTINGS.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        String key = "MENUS.SETTINGS.BUTTONS.";
        buttons.put(config.getInteger(key + "TOGGLESCOREBOARD.SLOT"), new SettingsButton(SettingsType.TOGGLESCOREBOARD));
        buttons.put(config.getInteger(key + "TOGGLEPINGONSCOREBOARD.SLOT"), new SettingsButton(SettingsType.TOGGLEPINGONSCOREBOARD));
        buttons.put(config.getInteger(key + "TOGGLECPSONSCOREBOARD.SLOT"), new SettingsButton(SettingsType.TOGGLECPSONSCOREBOARD));
        buttons.put(config.getInteger(key + "TOGGLESPECTATORS.SLOT"), new SettingsButton(SettingsType.TOGGLESPECTATORS));
        buttons.put(config.getInteger(key + "TOGGLESHOWPLAYERS.SLOT"), new SettingsButton(SettingsType.TOGGLESHOWPLAYERS));
        buttons.put(config.getInteger(key + "TOGGLEDUELREQUESTS.SLOT"), new SettingsButton(SettingsType.TOGGLEDUELREQUESTS));
        buttons.put(config.getInteger(key + "TOGGLETOURNAMENTMESSAGES.SLOT"), new SettingsButton(SettingsType.TOGGLETOURNAMENTMESSAGES));
        buttons.put(config.getInteger(key + "TOGGLETABSTYLE.SLOT"), new SettingsButton(SettingsType.TOGGLETABSTYLE));
        buttons.put(config.getInteger(key + "TOGGLEPINGFACTOR.SLOT"), new SettingsButton(SettingsType.TOGGLEPINGFACTOR));
        buttons.put(config.getInteger(key + "TOGGLELIGHTNING.SLOT"), new SettingsButton(SettingsType.TOGGLELIGHTNING));
        buttons.put(config.getInteger(key + "TOGGLEDROPPROTECT.SLOT"), new SettingsButton(SettingsType.TOGGLEDROPPROTECT));
        return buttons;
    }

    @AllArgsConstructor
    private static class SettingsButton extends Button {

        private final SettingsType type;

        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lines = new ArrayList<>();
            lines.add(CC.MENU_BAR);
            String key;
            switch (type) {
                case TOGGLESCOREBOARD:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLESCOREBOARD";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isScoreboardEnabled() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isScoreboardEnabled() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLEDUELREQUESTS:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLEDUELREQUESTS";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isReceiveDuelRequests() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isReceiveDuelRequests() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLEPINGFACTOR:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLEPINGFACTOR";
                    if (player.hasPermission("array.profile.pingfactor")) {
                        for ( String text : config.getStringList(key + ".LORE_PERMISSION" )) {
                            if (text.contains("<option>")) {
                                lines.add((profile.getSettings().isUsingPingFactor() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                                lines.add((!profile.getSettings().isUsingPingFactor() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                                continue;
                            }
                            lines.add(CC.translate(text));
                        }
                    } else {
                        config.getStringList(key + ".LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", Essentials.getSocialMeta().getStore()))));
                    }
                    break;
                case TOGGLESPECTATORS:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLESPECTATORS";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isAllowSpectators() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isAllowSpectators() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLELIGHTNING:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLELIGHTNING";
                    if (player.hasPermission("array.profile.lightning")) {
                        for ( String text : config.getStringList(key + ".LORE_PERMISSION" )) {
                            if (text.contains("<option>")) {
                                lines.add((profile.getSettings().isDeathLightning() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                                lines.add((!profile.getSettings().isDeathLightning() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                                continue;
                            }
                            lines.add(CC.translate(text));
                        }
                    } else {
                        config.getStringList(key + ".LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", Essentials.getSocialMeta().getStore()))));
                    }
                    break;
                case TOGGLEPINGONSCOREBOARD:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLEPINGONSCOREBOARD";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isPingScoreboard() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isPingScoreboard() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLECPSONSCOREBOARD:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLECPSONSCOREBOARD";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isCpsScoreboard() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isCpsScoreboard() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLETOURNAMENTMESSAGES:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLETOURNAMENTMESSAGES";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isAllowTournamentMessages() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isAllowTournamentMessages() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLETABSTYLE:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLETABSTYLE";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isVanillaTab() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isVanillaTab() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLESHOWPLAYERS:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLESHOWPLAYERS";
                    for ( String text : config.getStringList(key + ".LORE" )) {
                        if (text.contains("<option>")) {
                            lines.add((profile.getSettings().isShowPlayers() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isShowPlayers() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                    break;
                case TOGGLEDROPPROTECT:
                    key = "MENUS.SETTINGS.BUTTONS.TOGGLEDROPPROTECT";
                    if (player.hasPermission("array.profile.dropprotect")) {
                        for ( String text : config.getStringList(key + ".LORE_PERMISSION" )) {
                            if (text.contains("<option>")) {
                                lines.add((profile.getSettings().isPreventSword() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                                lines.add((!profile.getSettings().isPreventSword() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                                continue;
                            }
                            lines.add(CC.translate(text));
                        }
                    } else {
                        config.getStringList(key + ".LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", Essentials.getSocialMeta().getStore()))));
                    }
            }
            lines.add(CC.MENU_BAR);
            return new ItemBuilder(Material.valueOf(config.getString("MENUS.SETTINGS.BUTTONS." + type.name().toUpperCase() + ".MATERIAL"))).name(config.getString("MENUS.SETTINGS.BUTTONS." + type.name().toUpperCase() + ".NAME")).lore(lines).build();
        }

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
                        profile.getSettings().setUsingPingFactor(!profile.getSettings().isUsingPingFactor());
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
                    profile.getSettings().setAllowTournamentMessages(!profile.getSettings().isAllowTournamentMessages());
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

        @Override
        public boolean shouldUpdate(final Player player, final ClickType clickType) {
            return true;
        }
    }
}
