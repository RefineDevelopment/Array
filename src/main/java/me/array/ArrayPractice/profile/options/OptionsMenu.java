package me.array.ArrayPractice.profile.options;

import me.activated.core.menus.settings.SettingsMenu;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new OptionsButton(OptionsType.TOGGLESCOREBOARD));
        buttons.put(1, new OptionsButton(OptionsType.TOGGLETOURNAMENTMESSAGES));
        buttons.put(2, new OptionsButton(OptionsType.TOGGLESPECTATORS));
        buttons.put(3, new OptionsButton(OptionsType.TOGGLEPINGONSCOREBOARD));
        buttons.put(4, new OptionsButton(OptionsType.TOGGLELIGHTNING));
        buttons.put(5, new OptionsButton(OptionsType.TOGGLEPLAYERVISIBILITY));
        buttons.put(7, new OptionsButton(OptionsType.CORESETTINGS));
        buttons.put(6, new OptionsButton(OptionsType.TOGGLEPINGFACTOR));
        buttons.put(8, new OptionsButton(OptionsType.TOGGLEDUELREQUESTS));
        return buttons;
    }

    private static class OptionsButton extends Button
    {
        private final OptionsType type;

        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lines = new ArrayList<>();
            if (this.type == OptionsType.TOGGLESCOREBOARD) {
                lines.add("");
                lines.add("&7Enable or Disable Scoreboard");
                lines.add("&7Display for your profile.");
                lines.add("");
                lines.add((profile.getOptions().isShowScoreboard() ? "&a&l● " : "&8&l● ") +  "&fShow scoreboard");
                lines.add((!profile.getOptions().isShowScoreboard() ? "&a&l● " : "&8&l● ") + "&fHide scoreboard");
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                lines.add("");
                lines.add("&7Enable or Disable Duels from");
                lines.add("&7other players for your profile.");
                lines.add("");
                lines.add((profile.getOptions().isReceiveDuelRequests() ? "&a&l● " : "&8&l● ") +  "&fAllow Duels");
                lines.add((!profile.getOptions().isReceiveDuelRequests() ? "&a&l● " : "&8&l● ") + "&fDon't Allow Duels");
            }
            else if (this.type == OptionsType.TOGGLEPINGFACTOR) {
                if (player.hasPermission("practice.donator")) {
                    lines.add("");
                    lines.add("&7Enable or Disable queueing with");
                    lines.add("&7Players of similar ping as you.");
                    lines.add("");
                    lines.add((profile.getOptions().isUsingPingFactor() ? "&a&l● " : "&8&l● ") + "&fUse Ping Factor");
                    lines.add((!profile.getOptions().isUsingPingFactor() ? "&a&l● " : "&8&l● ") + "&fDon't Use Ping Factor");
                } else {
                    lines.add("");
                    lines.add("&7Enable or Disable queueing with");
                    lines.add("&7Players of similar ping as you.");
                    lines.add("");
                    lines.add("&7This Option is Donator only!");
                    lines.add("&7Please buy a rank at &bstore.resolve.rip.");
                }
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                lines.add("");
                lines.add("&7Enable or Disable Spectators");
                lines.add("&7on your Matches for your Profile.");
                lines.add("");
                lines.add((profile.getOptions().isAllowSpectators() ? "&a&l● " : "&8&l● ") + "&fAllow Spectators");
                lines.add((!profile.getOptions().isAllowSpectators() ? "&a&l● " : "&8&l● ") + "&fDon't Allow Spectators");
            } else if (this.type == OptionsType.TOGGLELIGHTNING) {
                if (player.hasPermission("practice.donator")) {
                    lines.add("");
                    lines.add("&7Enable or Disable Lightning");
                    lines.add("&7Death effect for your Profile.");
                    lines.add("");
                    lines.add((profile.getOptions().isLightning() ? "&a&l● " : "&8&l● ") + "&fEnable Lightning Death Effect");
                    lines.add((!profile.getOptions().isLightning() ? "&a&l● " : "&8&l● ") + "&fDisable Lightning Death Effect");
                } else {
                    lines.add("");
                    lines.add("&7Enable or Disable Lightning");
                    lines.add("&7Death effect for your Profile.");
                    lines.add("");
                    lines.add("&7This Option is Donator only!");
                    lines.add("&7Please buy a rank at &bstore.resolve.rip.");
                }
            } else if (this.type == OptionsType.CORESETTINGS) {
                lines.add("");
                lines.add("&7Click to view Profile Settings");
                lines.add("&7Ex: PMs, Sounds, Global Chat.");
                lines.add("");
            } else if (this.type == OptionsType.TOGGLEPINGONSCOREBOARD) {
                lines.add("");
                lines.add("&7Enable or Disable Ping on");
                lines.add("&7Scoreboard for your Profile.");
                lines.add("");
                lines.add((profile.getOptions().isPingScoreboard() ? "&a&l● " : "&8&l● ") + "&fShow Ping on Scoreboard");
                lines.add((!profile.getOptions().isPingScoreboard() ? "&a&l● " : "&8&l● ") + "&fDon't Show Ping on Scoreboard");
            } else if (this.type == OptionsType.TOGGLETOURNAMENTMESSAGES) {
                lines.add("");
                lines.add("&7Enable or Disable Tournament");
                lines.add("&7Messages for your Profile.");
                lines.add("");
                lines.add((profile.getOptions().isAllowTournamentMessages() ? "&a&l● " : "&8&l● ") + "&fShow Tournament Messages");
                lines.add((!profile.getOptions().isAllowTournamentMessages() ? "&a&l● " : "&8&l● ") + "&fDon't Show Tournament Messages");
            } else if (this.type == OptionsType.TOGGLEPLAYERVISIBILITY) {
                lines.add("");
                lines.add("&7Toggle Player Visibility");
                lines.add("&7in Lobby for your Profile.");
                lines.add("");
                lines.add((profile.getOptions().isAllowTournamentMessages() ? "&a&l● " : "&8&l● ") + "&fShow players at Lobby");
                lines.add((!profile.getOptions().isAllowTournamentMessages() ? "&a&l● " : "&8&l● ") + "&fDon't Show players at Lobby");
            }
            return new ItemBuilder(this.type.getMaterial()).name("&b" + this.type.getName()).lore(lines).build();
        }

        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (this.type == OptionsType.TOGGLESCOREBOARD) {
                profile.getOptions().setShowScoreboard(!profile.getOptions().isShowScoreboard());
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                profile.getOptions().setReceiveDuelRequests(!profile.getOptions().isReceiveDuelRequests());
            }
            else if (this.type == OptionsType.TOGGLEPINGFACTOR) {
                if (player.hasPermission("practice.donator+")) {
                    profile.getOptions().setUsingPingFactor(!profile.getOptions().isUsingPingFactor());
                } else {
                    player.closeInventory();
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                }
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                profile.getOptions().setAllowSpectators(!profile.getOptions().isAllowSpectators());
            }
            else if (this.type == OptionsType.TOGGLELIGHTNING) {
                if (player.hasPermission("practice.donator")) {
                    profile.getOptions().setLightning(!profile.getOptions().isLightning());
                } else {
                    player.closeInventory();
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                }
            } else if (this.type == OptionsType.CORESETTINGS) {
                new SettingsMenu().open(player);
            } else if (this.type == OptionsType.TOGGLEPINGONSCOREBOARD) {
                profile.getOptions().setPingScoreboard(!profile.getOptions().isPingScoreboard());
            } else if (this.type == OptionsType.TOGGLETOURNAMENTMESSAGES) {
                profile.getOptions().setAllowTournamentMessages(!profile.getOptions().isAllowTournamentMessages());
            } else if (this.type == OptionsType.TOGGLEPLAYERVISIBILITY) {
                profile.getOptions().setPlayerVisibility(!profile.getOptions().isPlayerVisibility());
                profile.handleVisibility();
            }
        }

        @Override
        public boolean shouldUpdate(final Player player, final ClickType clickType) {
            return true;
        }

        public OptionsButton(final OptionsType type) {
            this.type = type;
        }
    }
}
