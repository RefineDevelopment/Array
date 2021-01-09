package me.array.ArrayPractice.profile.options;

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
        buttons.put(2, new OptionsButton(OptionsType.TOGGLEDUELREQUESTS));
        buttons.put(4, new OptionsButton(OptionsType.TOGGLESPECTATORS));
        buttons.put(6, new OptionsButton(OptionsType.TOGGLEPINGFACTOR));
        buttons.put(8, new OptionsButton(OptionsType.TOGGLELIGHTNING));
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
                lines.add("&7Enable or Disable Game");
                lines.add("&7Scoreboard for your profile.");
                lines.add("");
                lines.add((profile.getOptions().isShowScoreboard() ? "&a&l● " : "&c&l● ") +  "&fShow scoreboard");
                lines.add((!profile.getOptions().isShowScoreboard() ? "&a&l● " : "&c&l● ") + "&fHide scoreboard");
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                lines.add("&7Enable or Disable Duels from");
                lines.add("&7other players for your profile.");
                lines.add("");
                lines.add((profile.getOptions().isReceiveDuelRequests() ? "&a&l● " : "&c&l● ") +  "&fAllow Duels");
                lines.add((!profile.getOptions().isReceiveDuelRequests() ? "&a&l● " : "&c&l● ") + "&fDon't Allow Duels");
            }
            else if (this.type == OptionsType.TOGGLEPINGFACTOR) {
                lines.add("&7Enable or Disable queueing with");
                lines.add("&7Players of similar ping as you.");
                lines.add("");
                lines.add((profile.getOptions().isUsingPingFactor() ? "&a&l● " : "&c&l● ") +  "&fUse Ping Factor");
                lines.add((!profile.getOptions().isUsingPingFactor() ? "&a&l● " : "&c&l● ") + "&fDon't Use Ping Factor");
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                lines.add("&7Enable or Disable Spectators");
                lines.add("&7on your Matches for your Profile.");
                lines.add("");
                lines.add((profile.getOptions().isAllowSpectators() ? "&a&l● " : "&c&l● ") + "&fAllow Spectators");
                lines.add((!profile.getOptions().isAllowSpectators() ? "&a&l● " : "&c&l● ") + "&fDon't Allow Spectators");
            } else {
                lines.add("&7Enable or Disable Lightning");
                lines.add("&7Death effect for your Profile.");
                lines.add("");
                lines.add((profile.getOptions().isLightning() ? "&a&l● " : "&c&l● ") + "&fEnable Lightning Death Effect");
                lines.add((!profile.getOptions().isLightning() ? "&a&l● " : "&c&l● ") + "&fDisable Lightning Death Effect");
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
                if (player.hasPermission("practice.donator")) {
                    profile.getOptions().setUsingPingFactor(!profile.getOptions().isUsingPingFactor());
                } else {
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
                    player.sendMessage(CC.translate("&7You do not have permission to use this setting."));
                    player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                }
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
