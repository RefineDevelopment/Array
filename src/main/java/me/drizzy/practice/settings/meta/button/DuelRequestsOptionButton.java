

package me.drizzy.practice.settings.meta.button;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.profile.option.menu.ProfileOptionButton;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class DuelRequestsOptionButton extends ProfileOptionButton
{
    @Override
    public ItemStack getEnabledItem(final Player player) {
        return new ItemBuilder(Material.BLAZE_ROD).build();
    }
    
    @Override
    public ItemStack getDisabledItem(final Player player) {
        return new ItemBuilder(Material.BLAZE_ROD).build();
    }
    
    @Override
    public String getOptionName() {
        return "&b&lDuel Requests";
    }
    
    @Override
    public String getDescription() {
        return "If enabled, you will receive duel requests.";
    }
    
    @Override
    public String getEnabledOption() {
        return "Receive duel requests";
    }
    
    @Override
    public String getDisabledOption() {
        return "Do not receive duel requests";
    }
    
    @Override
    public boolean isEnabled(final Player player) {
        return Profile.getByUuid(player.getUniqueId()).getSettings().isReceiveDuelRequests();
    }
    
    @Override
    public void clicked(final Player player, final ClickType clickType) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getSettings().setReceiveDuelRequests(!profile.getSettings().isReceiveDuelRequests());
    }
}
