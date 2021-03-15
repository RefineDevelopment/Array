package me.drizzy.practice.settings;

import me.drizzy.practice.enums.SettingsType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new SettingsButton(SettingsType.TOGGLESCOREBOARD));
        buttons.put(1, new SettingsButton(SettingsType.TOGGLEPINGONSCOREBOARD));
        buttons.put(2, new SettingsButton(SettingsType.TOGGLESPECTATORS));
        buttons.put(3, new SettingsButton(SettingsType.TOGGLESHOWPLAYERS));
        buttons.put(4, new SettingsButton(SettingsType.TOGGLEDUELREQUESTS));
        buttons.put(5, new SettingsButton(SettingsType.TOGGLETOURNAMENTMESSAGES));
        buttons.put(6, new SettingsButton(SettingsType.TOGGLEVANILLATAB));
        buttons.put(7, new SettingsButton(SettingsType.TOGGLEPINGFACTOR));
        buttons.put(8, new SettingsButton(SettingsType.TOGGLELIGHTNING));
        return buttons;
    }

    private static class SettingsButton extends Button
    {
        private final SettingsType type;

        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile=Profile.getByUuid(player.getUniqueId());
            List<String> lines=new ArrayList<>();
            lines.add(CC.MENU_BAR);
            switch (type) {
                case TOGGLESCOREBOARD:
                    lines.add("&7Enable or Disable Scoreboard");
                    lines.add("&7Display for your profile.");
                    lines.add("");
                    lines.add((profile.getSettings().isShowScoreboard() ? "&a&l■ " : "&8&l■ ") + "&fShow scoreboard");
                    lines.add((!profile.getSettings().isShowScoreboard() ? "&a&l■ " : "&8&l■ ") + "&fHide scoreboard");
                    break;
                case TOGGLEDUELREQUESTS:
                    lines.add("&7Enable or Disable Duels from");
                    lines.add("&7other players for your profile.");
                    lines.add("");
                    lines.add((profile.getSettings().isReceiveDuelRequests() ? "&a&l■ " : "&8&l■ ") + "&fAllow Duels");
                    lines.add((!profile.getSettings().isReceiveDuelRequests() ? "&a&l■ " : "&8&l■ ") + "&fDon't Allow Duels");
                    break;
                case TOGGLEPINGFACTOR:
                    if (player.hasPermission("array.donator+")) {
                        lines.add("&7Enable or Disable queueing with");
                        lines.add("&7players of similar ping as you.");
                        lines.add("");
                        lines.add((profile.getSettings().isUsingPingFactor() ? "&a&l■ " : "&8&l■ ") + "&fUse Ping Factor");
                        lines.add((!profile.getSettings().isUsingPingFactor() ? "&a&l■ " : "&8&l■ ") + "&fDon't Use Ping Factor");
                    } else {
                        lines.add("&7Enable or Disable queueing with");
                        lines.add("&7players of similar ping as you.");
                        lines.add("");
                        lines.add("&7This Option is Donator only!");
                        lines.add("&7Please Upgrade your rank at &bstore.purgemc.club.");
                    }
                    break;
                case TOGGLESPECTATORS:
                    lines.add("&7Enable or Disable Spectators");
                    lines.add("&7on your Matches for your Profile.");
                    lines.add("");
                    lines.add((profile.getSettings().isAllowSpectators() ? "&a&l■ " : "&8&l■ ") + "&fAllow Spectators");
                    lines.add((!profile.getSettings().isAllowSpectators() ? "&a&l■ " : "&8&l■ ") + "&fDon't Allow Spectators");
                    break;
                case TOGGLELIGHTNING:
                    if (player.hasPermission("array.donator")) {
                        lines.add("&7Enable or Disable Lightning");
                        lines.add("&7Death effect for your Profile.");
                        lines.add("");
                        lines.add((profile.getSettings().isLightning() ? "&a&l■ " : "&8&l■ ") + "&fEnable Lightning Death Effect");
                        lines.add((!profile.getSettings().isLightning() ? "&a&l■ " : "&8&l■ ") + "&fDisable Lightning Death Effect");
                    } else {
                        lines.add("&7Enable or Disable Lightning");
                        lines.add("&7Death effect for your Profile.");
                        lines.add("");
                        lines.add("&7This Option is Donator only!");
                        lines.add("&7Please Upgrade your rank at &bstore.purgemc.club.");
                    }
                    break;
                case TOGGLEPINGONSCOREBOARD:
                    lines.add("&7Enable or Disable Ping on");
                    lines.add("&7Scoreboard for your Profile.");
                    lines.add("");
                    lines.add((profile.getSettings().isPingScoreboard() ? "&a&l■ " : "&8&l■ ") + "&fShow Ping on Scoreboard");
                    lines.add((!profile.getSettings().isPingScoreboard() ? "&a&l■ " : "&8&l■ ") + "&fDon't Show Ping on Scoreboard");
                    break;
                case TOGGLETOURNAMENTMESSAGES:
                    lines.add("&7Enable or Disable Tournament");
                    lines.add("&7Messages for your Profile.");
                    lines.add("");
                    lines.add((profile.getSettings().isAllowTournamentMessages() ? "&a&l■ " : "&8&l■ ") + "&fShow Tournament Messages");
                    lines.add((!profile.getSettings().isAllowTournamentMessages() ? "&a&l■ " : "&8&l■ ") + "&fDon't Show Tournament Messages");
                    break;
                case TOGGLEVANILLATAB:
                    lines.add("&7Toggle through different");
                    lines.add("&7Tab Styles for your profile");
                    lines.add("");
                    lines.add((profile.getSettings().isVanillaTab() ? "&a&l■ " : "&8&l■ ") + "&fShow Vanilla Tab");
                    lines.add((!profile.getSettings().isVanillaTab() ? "&a&l■ " : "&8&l■ ") + "&fShow Default Tab");
                    break;
                case TOGGLESHOWPLAYERS:
                    lines.add("&7Toggle player visibility");
                    lines.add("&7at Lobby for your profile");
                    lines.add("");
                    lines.add((profile.getSettings().isShowPlayers() ? "&a&l■ " : "&8&l■ ") + "&fShow Players at Lobby");
                    lines.add((!profile.getSettings().isShowPlayers() ? "&a&l■ " : "&8&l■ ") + "&fDon't Show Players at Lobby");
                    break;
            }
            lines.add(CC.MENU_BAR);
            return new ItemBuilder(this.type.getMaterial()).name("&b" + this.type.getName()).lore(lines).build();
        }

        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            switch (type) {
                case TOGGLESCOREBOARD:
                    Button.playSuccess(player);
                    profile.getSettings().setShowScoreboard(!profile.getSettings().isShowScoreboard());
                    break;
                case TOGGLEDUELREQUESTS:
                    Button.playSuccess(player);
                    profile.getSettings().setReceiveDuelRequests(!profile.getSettings().isReceiveDuelRequests());
                    break;

                case TOGGLEPINGFACTOR:
                if (player.hasPermission("array.donator+")) {
                    Button.playSuccess(player);
                    profile.getSettings().setUsingPingFactor(!profile.getSettings().isUsingPingFactor());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                }
                    break;
                case TOGGLESPECTATORS:
                    Button.playSuccess(player);
                    profile.getSettings().setAllowSpectators(!profile.getSettings().isAllowSpectators());
                    break;
                case TOGGLELIGHTNING:
                if (player.hasPermission("array.donator")) {
                    Button.playSuccess(player);
                    profile.getSettings().setLightning(!profile.getSettings().isLightning());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.purgemc.club &7!"));
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
                case TOGGLEVANILLATAB:
                    Button.playSuccess(player);
                    profile.getSettings().setVanillaTab(!profile.getSettings().isVanillaTab());
                    break;
                case TOGGLESHOWPLAYERS:
                    Button.playSuccess(player);
                    profile.getSettings().setShowPlayers(!profile.getSettings().isShowPlayers());
                    profile.handleVisibility();
                    break;
            }
        }

        @Override
        public boolean shouldUpdate(final Player player, final ClickType clickType) {
            return true;
        }

        public SettingsButton(final SettingsType type) {
            this.type = type;
        }
    }
}
