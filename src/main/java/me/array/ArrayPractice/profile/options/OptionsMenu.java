package me.array.ArrayPractice.profile.options;

import java.beans.ConstructorProperties;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class OptionsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&bOptions";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(1, new OptionsButton(OptionsType.TOGGLESCOREBOARD));
        buttons.put(3, new OptionsButton(OptionsType.TOGGLEDUELREQUESTS));
        buttons.put(5, new OptionsButton(OptionsType.TOGGLESPECTATORS));
        buttons.put(7, new OptionsButton(OptionsType.TOGGLEPMS));
        return buttons;
    }
    
    private static class OptionsButton extends Button
    {
        private final OptionsType type;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            boolean lore;
            if (this.type == OptionsType.TOGGLESCOREBOARD) {
                lore = profile.getOptions().isShowScoreboard();
            }
            else if (this.type == OptionsType.TOGGLEDUELREQUESTS) {
                lore = profile.getOptions().isReceiveDuelRequests();
            }
            else if (this.type == OptionsType.TOGGLEPMS) {
                lore = profile.getOptions().isPrivateMessages();
            }
            else {
                lore = profile.getOptions().isAllowSpectators();
            }
            return new ItemBuilder(this.type.getMaterial()).name("&b" + this.type.getName()).lore("&fEnabled: " + (lore ? "&a&l■ " : "&c&l■ ") + lore).build();
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
                profile.getOptions().setPrivateMessages(!profile.getOptions().isPrivateMessages());
            }
            else {
                profile.getOptions().setAllowSpectators(!profile.getOptions().isAllowSpectators());
            }
        }
        
        @Override
        public boolean shouldUpdate(final Player player, final ClickType clickType) {
            return true;
        }
        
        @ConstructorProperties({ "type" })
        public OptionsButton(final OptionsType type) {
            this.type = type;
        }
    }
}
