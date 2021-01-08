package me.array.ArrayPractice.profile.options;

import me.activated.core.data.other.systems.MessageSystem;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.entity.*;
import me.array.ArrayPractice.util.external.menu.*;
import java.util.*;
import org.bukkit.inventory.*;
import me.array.ArrayPractice.profile.*;
import me.array.ArrayPractice.util.external.*;
import org.bukkit.event.inventory.*;

public class OptionsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&4Options";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new OptionsButton(OptionsType.TOGGLESCOREBOARD));
        buttons.put(2, new OptionsButton(OptionsType.TOGGLEDUELREQUESTS));
        buttons.put(4, new OptionsButton(OptionsType.TOGGLESPECTATORS));
        buttons.put(6, new OptionsButton(OptionsType.TOGGLEPMS));
        buttons.put(8, new OptionsButton(OptionsType.TOGGLELIGHTNING));
        return buttons;
    }

    private static class OptionsButton extends Button
    {
        private final OptionsType type;

        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            final MessageSystem messages =AquaCoreAPI.INSTANCE.getGlobalPlayer(player.getUniqueId()).getMessageSystem();
            List<String> lines = new ArrayList<>();
            if (this.type == OptionsType.TOGGLESCOREBOARD) {
                lines.add((profile.getOptions().isShowScoreboard() ? "&a&l● " : "&c&l● ") +  "&fShow scoreboard");
                lines.add((!profile.getOptions().isShowScoreboard() ? "&a&l● " : "&c&l● ") + "&fHide scoreboard");
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                lines.add((profile.getOptions().isReceiveDuelRequests() ? "&a&l● " : "&c&l● ") +  "&fAllow Duels");
                lines.add((!profile.getOptions().isReceiveDuelRequests() ? "&a&l● " : "&c&l● ") + "&fDon't Allow Duels");
            }
            else if (this.type == OptionsType.TOGGLEPMS) {
                lines.add((messages.isMessagesToggled() ? "&a&l● " : "&c&l● ") + "&fAllow Private Messages");
                lines.add((!messages.isMessagesToggled() ? "&a&l● " : "&c&l● ") + "&fDon't Allow Private Messages");
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                lines.add((profile.getOptions().isAllowSpectators() ? "&a&l● " : "&c&l● ") + "&fAllow Spectators");
                lines.add((!profile.getOptions().isAllowSpectators() ? "&a&l● " : "&c&l● ") + "&fDon't Allow Spectators");
            } else {
                lines.add((profile.getOptions().isAllowSpectators() ? "&a&l● " : "&c&l● ") + "&fEnable Lightning Death Effect");
                lines.add((!profile.getOptions().isAllowSpectators() ? "&a&l● " : "&c&l● ") + "&fDisable Lightning Death Effect");
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
            else if (this.type == OptionsType.TOGGLEPMS) {
                player.performCommand("togglepm");
            }
            else if (this.type == OptionsType.TOGGLESPECTATORS) {
                profile.getOptions().setAllowSpectators(!profile.getOptions().isAllowSpectators());
            }
            else if (this.type == OptionsType.TOGGLELIGHTNING) {
                profile.getOptions().setLightning(!profile.getOptions().isLightning());
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
