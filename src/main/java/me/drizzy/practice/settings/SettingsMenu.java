package me.drizzy.practice.settings;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
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
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lines = new ArrayList<>();
            lines.add(CC.MENU_BAR);
            if (this.type == SettingsType.TOGGLESCOREBOARD) {
                lines.add("&7Enable or Disable Scoreboard");
                lines.add("&7Display for your profile.");
                lines.add("");
                lines.add((profile.getSettings().isShowScoreboard() ? "&b&l▶ " : " ") + "&fShow scoreboard");
                lines.add((!profile.getSettings().isShowScoreboard() ? "&b&l▶ " : " ") + "&fHide scoreboard");
            }
            else if (this.type == SettingsType.TOGGLEDUELREQUESTS) {
                lines.add("&7Enable or Disable Duels from");
                lines.add("&7other players for your profile.");
                lines.add("");
                lines.add((profile.getSettings().isReceiveDuelRequests() ? "&b&l▶ " : " ") + "&fAllow Duels");
                lines.add((!profile.getSettings().isReceiveDuelRequests() ? "&b&l▶ " : " ") + "&fDon't Allow Duels");
            }
            else if (this.type == SettingsType.TOGGLEPINGFACTOR) {
                if (player.hasPermission("practice.donator+")) {
                    lines.add("&7Enable or Disable queueing with");
                    lines.add("&7Players of similar ping as you.");
                    lines.add("");
                    lines.add((profile.getSettings().isUsingPingFactor() ? "&b&l▶ " : " ") + "&fUse Ping Factor");
                    lines.add((!profile.getSettings().isUsingPingFactor() ? "&b&l▶ " : " ") + "&fDon't Use Ping Factor");
                } else {
                    lines.add("&7Enable or Disable queueing with");
                    lines.add("&7players of similar ping as you.");
                    lines.add("");
                    lines.add("&7This Option is Donator only!");
                    lines.add("&7Please Upgrade your rank at &bstore.resolve.rip.");
                }
            }
            else if (this.type == SettingsType.TOGGLESPECTATORS) {
                lines.add("&7Enable or Disable Spectators");
                lines.add("&7on your Matches for your Profile.");
                lines.add("");
                lines.add((profile.getSettings().isAllowSpectators() ? "&b&l▶ " : " ") + "&fAllow Spectators");
                lines.add((!profile.getSettings().isAllowSpectators() ? "&b&l▶ " : " ") + "&fDon't Allow Spectators");
            } else if (this.type == SettingsType.TOGGLELIGHTNING) {
                if (player.hasPermission("practice.donator")) {
                    lines.add("&7Enable or Disable Lightning");
                    lines.add("&7Death effect for your Profile.");
                    lines.add("");
                    lines.add((profile.getSettings().isLightning() ? "&b&l▶ " : " ") + "&fEnable Lightning Death Effect");
                    lines.add((!profile.getSettings().isLightning() ? "&b&l▶ " : " ") + "&fDisable Lightning Death Effect");
                } else {
                    lines.add("&7Enable or Disable Lightning");
                    lines.add("&7Death effect for your Profile.");
                    lines.add("");
                    lines.add("&7This Option is Donator only!");
                    lines.add("&7Please Upgrade your rank at &bstore.resolve.rip.");
                }
            } else if (this.type == SettingsType.CORESETTINGS) {
                lines.add("&7Click to view Profile Settings");
                lines.add("&7Ex: PMs, Sounds, Global Chat.");
                lines.add("");
            } else if (this.type == SettingsType.TOGGLEPINGONSCOREBOARD) {
                lines.add("&7Enable or Disable Ping on");
                lines.add("&7Scoreboard for your Profile.");
                lines.add("");
                lines.add((profile.getSettings().isPingScoreboard() ? "&b&l▶ " : " ") + "&fShow Ping on Scoreboard");
                lines.add((!profile.getSettings().isPingScoreboard() ? "&b&l▶ " : " ") + "&fDon't Show Ping on Scoreboard");
            } else if (this.type == SettingsType.TOGGLETOURNAMENTMESSAGES) {
                lines.add("&7Enable or Disable Tournament");
                lines.add("&7Messages for your Profile.");
                lines.add("");
                lines.add((profile.getSettings().isAllowTournamentMessages() ? "&b&l▶ " : " ") + "&fShow Tournament Messages");
                lines.add((!profile.getSettings().isAllowTournamentMessages() ? "&b&l▶ " : " ") + "&fDon't Show Tournament Messages");
            } else if (this.type == SettingsType.TOGGLEVANILLATAB) {
                lines.add("&7Toggle through different");
                lines.add("&7Tab Styles for your profile");
                lines.add("");
                lines.add((profile.getSettings().isVanillaTab() ? "&b&l▶ " : " ") + "&fShow Vanilla Tab");
                lines.add((!profile.getSettings().isVanillaTab() ? "&b&l▶ " : " ") + "&fShow Normal Tab");
            } else if (this.type == SettingsType.TOGGLESHOWPLAYERS) {
                lines.add("&7Toggle player visibility");
                lines.add("&7at Lobby for your profile");
                lines.add("");
                lines.add("&cThis option is currently in development!");
            }
            lines.add(CC.MENU_BAR);
            return new ItemBuilder(this.type.getMaterial()).name("&b" + this.type.getName()).lore(lines).build();
        }

        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (this.type == SettingsType.TOGGLESCOREBOARD) {
                Button.playSuccess(player);
                profile.getSettings().setShowScoreboard(!profile.getSettings().isShowScoreboard());
            }
            else if (this.type == SettingsType.TOGGLEDUELREQUESTS) {
                Button.playSuccess(player);
                profile.getSettings().setReceiveDuelRequests(!profile.getSettings().isReceiveDuelRequests());
            }
            else if (this.type == SettingsType.TOGGLEPINGFACTOR) {
                if (player.hasPermission("practice.donator+")) {
                    Button.playSuccess(player);
                    profile.getSettings().setUsingPingFactor(!profile.getSettings().isUsingPingFactor());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.resolve.rip &7!"));
                }
            }
            else if (this.type == SettingsType.TOGGLESPECTATORS) {
                Button.playSuccess(player);
                profile.getSettings().setAllowSpectators(!profile.getSettings().isAllowSpectators());
            }
            else if (this.type == SettingsType.TOGGLELIGHTNING) {
                if (player.hasPermission("practice.donator")) {
                    Button.playSuccess(player);
                    profile.getSettings().setLightning(!profile.getSettings().isLightning());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                }
            } else if (this.type == SettingsType.CORESETTINGS) {
                Button.playSuccess(player);
                new me.activated.core.menus.settings.SettingsMenu().open(player);
            } else if (this.type == SettingsType.TOGGLEPINGONSCOREBOARD) {
                Button.playSuccess(player);
                profile.getSettings().setPingScoreboard(!profile.getSettings().isPingScoreboard());
            } else if (this.type == SettingsType.TOGGLETOURNAMENTMESSAGES) {
                Button.playSuccess(player);
                profile.getSettings().setAllowTournamentMessages(!profile.getSettings().isAllowTournamentMessages());
            } else if (this.type == SettingsType.TOGGLEVANILLATAB) {
                Button.playSuccess(player);
                profile.getSettings().setVanillaTab(!profile.getSettings().isVanillaTab());
            }/* else if (this.type == SettingsType.TOGGLESHOWPLAYERS) {
                Button.playSuccess(player);
                profile.getSettings().setShowPlayers(!profile.getSettings().isShowPlayers());
                for ( Player pls : Bukkit.getOnlinePlayers()) {
                    if (profile.getSettings().isShowPlayers()) {
                        player.showPlayer(pls);
                        NameTags.color(player, pls, ChatColor.GREEN, false);
                        if (!Profile.getByUuid(pls).isBusy(pls)) {
                            NameTags.color(pls, player, ChatColor.GREEN, false);
                        }
                    } else {
                        player.hidePlayer(pls);
                    }
                }*/
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
